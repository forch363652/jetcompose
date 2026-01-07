package com.joker.coolmall.core.data.usecase

import com.joker.coolmall.core.data.repository.FakeUserInfoRepository
import com.joker.coolmall.core.data.repository.UserInfoStoreRepository
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 获取用户资料的 UseCase
 * 
 * 职责：
 * - 处理数据优先级逻辑：AppState（内存） -> 本地存储 -> 预览数据
 * - 统一返回 Result<User>，避免 UI 层知道数据兜底细节
 * 
 * 数据优先级说明：
 * 1. AppState.userInfo：内存中的最新状态（最快，通常是最新的）
 * 2. UserInfoStoreRepository：本地持久化存储（备份）
 * 3. FakeUserInfoRepository：预览数据（仅用于开发/测试）
 */
class GetUserProfileUseCase @Inject constructor(
    private val appState: AppState,
    private val userInfoStoreRepository: UserInfoStoreRepository,
    private val fakeUserInfoRepository: FakeUserInfoRepository,
) {
    /**
     * 获取用户资料
     * 
     * @return Result<User> 包含用户信息的结果
     */
    suspend operator fun invoke(): Result<User> = withContext(Dispatchers.IO) {
        try {
            // 优先级 1：从 AppState（内存）获取
            val userFromMemory = appState.userInfo.value
            if (userFromMemory != null) {
                return@withContext Result.Success(userFromMemory)
            }

            // 优先级 2：从本地存储获取
            val userFromStorage = userInfoStoreRepository.getUserInfo()
            if (userFromStorage != null) {
                return@withContext Result.Success(userFromStorage)
            }

            // 优先级 3：使用预览数据（仅用于开发/测试）
            val previewUser = fakeUserInfoRepository.getPreviewUser()
            Result.Success(previewUser)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

