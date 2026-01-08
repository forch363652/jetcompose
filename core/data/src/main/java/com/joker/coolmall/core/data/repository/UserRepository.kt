package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.data.repository.SessionRepository
import com.joker.coolmall.core.data.repository.UserInfoStoreRepository
import com.joker.coolmall.core.database.dao.UserProfileDao
import com.joker.coolmall.core.database.entity.UserProfileEntity
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.datasource.userinfo.UserInfoNetworkDataSource
import com.joker.coolmall.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户资料仓库（offline-first）
 * 先读 Room 缓存，再网络刷新
 */
@Singleton
class UserRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val userInfoNetworkDataSource: UserInfoNetworkDataSource,
    private val sessionRepository: SessionRepository,
    private val userInfoStoreRepository: UserInfoStoreRepository
) {
    /**
     * 获取用户资料流（offline-first）
     * 先返回本地缓存，然后从网络刷新
     */
    fun getUserProfile(): Flow<Result<User>> = flow {
        // 1. 先读取本地缓存
        val userId = sessionRepository.userId.first()
        if (userId > 0) {
            val cachedEntity = userProfileDao.getUserProfileSync(userId)
            if (cachedEntity != null) {
                emit(Result.Success(cachedEntity.toUser()))
            }
        }

        // 2. 从网络刷新
        val response = userInfoNetworkDataSource.getPersonInfo()
        if (response.isSucceeded) {
            response.data?.let { user ->
                // 保存到本地
                if (user.id > 0) {
                    userProfileDao.insertOrUpdate(UserProfileEntity.fromUser(user))
                }
                emit(Result.Success(user))
            } ?: emit(Result.Error(Exception("获取用户信息失败：数据为空")))
        } else {
            emit(Result.Error(Exception(response.message ?: "获取用户信息失败")))
        }
    }.catch { e: Throwable ->
        emit(Result.Error(e) as Result<User>)
    }

    /**
     * 刷新用户资料（强制从网络获取）
     */
    suspend fun refresh(): Result<User> {
        return try {
            val response = userInfoNetworkDataSource.getPersonInfo()
            if (response.isSucceeded) {
                response.data?.let { user ->
                    // 保存到本地
                    if (user.id > 0) {
                        userProfileDao.insertOrUpdate(UserProfileEntity.fromUser(user))
                    }
                    Result.Success(user)
                } ?: Result.Error(Exception("刷新用户信息失败：数据为空"))
            } else {
                Result.Error(Exception(response.message ?: "刷新用户信息失败"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * 更新用户信息
     */
    fun updateUserInfo(params: Map<String, Any>): Flow<Result<Any>> = flow {
        val response = userInfoNetworkDataSource.updatePersonInfo(params)
        if (response.isSucceeded) {
            // 更新成功后刷新用户资料
            refresh()
            emit(Result.Success(response.data ?: Unit))
        } else {
            emit(Result.Error(Exception(response.message ?: "更新用户信息失败")))
        }
    }.catch { e: Throwable ->
        emit(Result.Error(e) as Result<Any>)
    }

    /**
     * 更新用户ID（三条ID）- 乐观更新模式
     * 
     * 策略：
     * 1. 立即更新本地数据库（乐观更新）
     * 2. 立即返回成功，让UI快速响应
     * 3. 后台尝试同步到服务器
     * 4. 如果失败，标记为待同步状态（可选：可以添加重试机制）
     * 
     * @param newId 新的ID
     * @return Result<Unit> 立即返回成功（本地已更新）
     */
    suspend fun updateUserId(newId: String): Result<Unit> {
        // 获取当前会话（同步方法，避免 Flow 等待问题）
        val currentSession = sessionRepository.getCurrentSession()
        var userId = currentSession.userId
        
        // Fallback 1: 如果 Session 未初始化（userId <= 0），尝试从本地数据库获取用户ID
        if (userId <= 0) {
            val firstUser = userProfileDao.getFirstUserProfile()
            if (firstUser != null) {
                userId = firstUser.id
            }
        }
        
        // Fallback 2: 如果数据库也没有，尝试从 MMKV 存储获取（UserInfoStoreRepository）
        if (userId <= 0) {
            userId = userInfoStoreRepository.getUserId()
        }
        
        // Fallback 3: 如果 MMKV 也没有，尝试从网络获取用户信息并保存
        if (userId <= 0) {
            val userResult = refresh()
            if (userResult is Result.Success) {
                userId = userResult.data.id
            }
        }
        
        // 最终检查
        if (userId <= 0) {
            return Result.Error(Exception("用户未登录，请先登录"))
        }

        // 1. 乐观更新：立即更新本地数据库
        val cachedEntity = userProfileDao.getUserProfileSync(userId)
        if (cachedEntity != null) {
            // 更新 unionid 字段（假设三条ID存储在 unionid 中）
            // 注意：如果ID存储在其他字段，需要相应调整
            val updatedEntity = cachedEntity.copy(
                unionid = newId,
                lastUpdated = System.currentTimeMillis()
            )
            userProfileDao.insertOrUpdate(updatedEntity)
        } else {
            // 如果本地没有缓存，创建一个新的实体
            // 注意：这里需要从其他地方获取完整的用户信息
            // 为了简化，我们假设至少有一个缓存实体
            return Result.Error(Exception("本地用户信息不存在，请先刷新"))
        }

        // 2. 后台尝试同步到服务器（不阻塞UI）
        try {
            val response = userInfoNetworkDataSource.updatePersonInfo(
                mapOf("userID" to newId) // 根据实际API调整字段名
            )
            
            if (response.isSucceeded) {
                // 同步成功，刷新完整用户信息以确保一致性
                refresh()
            } else {
                // 同步失败，但本地已更新
                // 可以在这里添加：
                // - 标记为待同步状态
                // - 记录到待同步队列
                // - 下次有网络时自动重试
                // 目前先记录日志，不影响用户体验
                android.util.Log.w("UserRepository", "ID同步失败，但本地已更新: ${response.message}")
            }
        } catch (e: Exception) {
            // 网络异常，本地已更新
            // 可以在这里添加重试机制
            android.util.Log.w("UserRepository", "ID同步异常，但本地已更新: ${e.message}")
        }

        // 3. 立即返回成功（本地已更新）
        return Result.Success(Unit)
    }

    /**
     * 清除用户资料（登出时使用）
     */
    suspend fun clearUserProfile() {
        val userId = sessionRepository.userId.first()
        if (userId > 0) {
            userProfileDao.deleteUserProfile(userId)
        }
    }
}

