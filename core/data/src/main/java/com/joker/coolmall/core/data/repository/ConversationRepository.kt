package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.database.dao.ConversationDao
import com.joker.coolmall.core.database.entity.ConversationEntity
import com.joker.coolmall.core.model.entity.CsSession
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.network.datasource.cs.CustomerServiceNetworkDataSource
import com.joker.coolmall.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 会话仓库（offline-first）
 * 先读 Room 缓存，再网络刷新
 */
@Singleton
class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val customerServiceNetworkDataSource: CustomerServiceNetworkDataSource
) {
    /**
     * 获取会话列表流（offline-first）
     * 先返回本地缓存，然后从网络刷新
     * 
     * 注意：目前使用本地缓存，网络接口待实现
     */
    fun getConversations(): Flow<Result<List<ConversationEntity>>> =
        conversationDao.getAllConversations()
            .map { conversations: List<ConversationEntity> ->
                Result.Success(conversations) as Result<List<ConversationEntity>>
            }
            .catch { e: Throwable ->
                emit(Result.Error(e) as Result<List<ConversationEntity>>)
            }

    /**
     * 刷新会话列表（强制从网络获取）
     * 
     * 注意：目前使用会话详情接口，后续需要实现会话列表接口
     */
    suspend fun refresh(): Result<List<ConversationEntity>> {
        return try {
            // TODO: 实现会话列表接口后替换此逻辑
            // 目前先获取会话详情作为占位
            val response = customerServiceNetworkDataSource.getSessionDetail()
            if (response.isSucceeded && response.data != null) {
                val session = response.data
                // 转换为实体并保存到本地
                val lastMessageText = session?.lastMsg?.content?.data ?: ""
                val entity = ConversationEntity(
                    id = session?.id.toString(),
                    name = session?.nickName?.ifEmpty { "客服" } ?: "",
                    avatarUrl = session?.avatarUrl.takeIf { it!!.isNotEmpty() },
                    lastMessage = lastMessageText,
                    timestamp = session?.updateTime?.let {
                        // 解析时间戳，这里需要根据实际格式调整
                        System.currentTimeMillis() 
                    } ?: 0,
                    unreadCount = session?.adminUnreadCount?.toInt() ?: 123,
                    isGroup = false,
                    isPinned = false,
                    lastUpdated = System.currentTimeMillis()
                )
                conversationDao.insertOrUpdate(entity)
                Result.Success(listOf(entity))
            } else {
                Result.Error(Exception(response.message ?: "刷新会话列表失败"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * 增量刷新（根据最后更新时间）
     */
    suspend fun refreshIncremental(): Result<List<ConversationEntity>> {
        return try {
            val lastUpdated = conversationDao.getLastUpdatedTime() ?: 0
            // TODO: 调用增量同步接口（如果有）
            // 目前先使用全量刷新
            refresh()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * 清除所有会话（登出时使用）
     */
    suspend fun clearAll() {
        conversationDao.clearAll()
    }

    /**
     * 更新未读消息数
     */
    suspend fun updateUnreadCount(conversationId: String, count: Int) {
        conversationDao.updateUnreadCount(conversationId, count)
    }

    /**
     * 标记所有消息为已读
     */
    suspend fun markAllAsRead() {
        conversationDao.markAllAsRead()
    }
}

