package com.joker.coolmall.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joker.coolmall.core.database.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 会话数据访问对象
 */
@Dao
interface ConversationDao {
    /**
     * 插入或更新会话
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(conversation: ConversationEntity)

    /**
     * 批量插入或更新会话
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(conversations: List<ConversationEntity>)

    /**
     * 更新会话
     */
    @Update
    suspend fun update(conversation: ConversationEntity)

    /**
     * 获取所有会话（按置顶和时间排序）
     */
    @Query("""
        SELECT * FROM conversations 
        ORDER BY isPinned DESC, timestamp DESC
    """)
    fun getAllConversations(): Flow<List<ConversationEntity>>

    /**
     * 根据ID获取会话
     */
    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    /**
     * 删除会话
     */
    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    /**
     * 清空所有会话
     */
    @Query("DELETE FROM conversations")
    suspend fun clearAll()

    /**
     * 获取最后更新时间（用于增量同步）
     */
    @Query("SELECT MAX(lastUpdated) FROM conversations")
    suspend fun getLastUpdatedTime(): Long?

    /**
     * 更新未读消息数
     */
    @Query("UPDATE conversations SET unreadCount = :count WHERE id = :conversationId")
    suspend fun updateUnreadCount(conversationId: String, count: Int)

    /**
     * 标记所有消息为已读
     */
    @Query("UPDATE conversations SET unreadCount = 0")
    suspend fun markAllAsRead()
}

