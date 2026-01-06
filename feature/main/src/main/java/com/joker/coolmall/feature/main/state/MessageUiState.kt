package com.joker.coolmall.feature.main.state

import com.joker.coolmall.core.network.monitor.NetworkStatus
import com.joker.coolmall.feature.main.viewmodel.ConnectionState

/**
 * 消息页 UI 状态（参考 NIA 的 UiState 模式）
 *
 * 管理消息页的所有 UI 状态，包括：
 * - 消息列表数据
 * - 是否有置顶消息
 * - 是否在下拉刷新
 * - 网络状态
 * - 连接状态
 * - 同步状态
 */
sealed interface MessageUiState {
    /**
     * 加载中状态
     */
    data object Loading : MessageUiState

    /**
     * 成功状态（有数据）
     */
    data class Success(
        /**
         * 会话列表
         */
        val conversations: List<ConversationItem> = emptyList(),

        /**
         * 是否有置顶消息
         */
        val hasPinnedMessages: Boolean = false,

        /**
         * 是否正在下拉刷新
         */
        val isRefreshing: Boolean = false,

        /**
         * 网络状态
         */
        val networkStatus: NetworkStatus = NetworkStatus.Available,

        /**
         * 连接状态
         */
        val connectionState: ConnectionState = ConnectionState.Connected,

        /**
         * 是否正在同步
         */
        val isSyncing: Boolean = false,
    ) : MessageUiState {
        /**
         * 会话总数
         */
        val conversationCount: Int
            get() = conversations.size

        /**
         * 是否显示加载图标
         * 条件：有内容 && 不在下拉刷新 && 没有置顶消息
         */
        val shouldShowLoadingIcon: Boolean
            get() = conversations.isNotEmpty() && !isRefreshing && !hasPinnedMessages

        /**
         * 是否显示头像
         * 条件：有内容 && 有置顶消息
         */
        val shouldShowAvatar: Boolean
            get() = conversations.isNotEmpty() && hasPinnedMessages
    }

    /**
     * 错误状态
     */
    data class Error(
        val message: String? = null,
    ) : MessageUiState
}

/**
 * 会话项数据类
 */
data class ConversationItem(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    /**
     * 是否置顶
     */
    val isPinned: Boolean = false,
)

