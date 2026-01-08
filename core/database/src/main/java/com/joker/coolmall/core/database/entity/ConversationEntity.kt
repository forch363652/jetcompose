package com.joker.coolmall.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 会话数据库实体
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,

    /**
     * 会话名称（用户昵称或群名称）
     */
    val name: String,

    /**
     * 头像URL
     */
    val avatarUrl: String? = null,

    /**
     * 最后一条消息
     */
    val lastMessage: String? = null,

    /**
     * 最后消息时间戳（毫秒）
     */
    val timestamp: Long = 0,

    /**
     * 未读消息数
     */
    val unreadCount: Int = 0,

    /**
     * 是否为群聊
     */
    val isGroup: Boolean = false,

    /**
     * 是否置顶
     */
    val isPinned: Boolean = false,

    /**
     * 数据更新时间（用于增量同步）
     */
    val lastUpdated: Long = System.currentTimeMillis()
)

