package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.network.monitor.NetworkMonitor
import com.joker.coolmall.core.network.monitor.NetworkStatus
import com.joker.coolmall.feature.main.state.ConversationItem
import com.joker.coolmall.feature.main.state.MessageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 消息页 ViewModel（参考 NIA 架构）
 *
 * 管理消息页的所有状态，包括：
 * - 消息列表
 * - 网络状态
 * - 连接状态
 * - 同步状态
 * - 下拉刷新状态
 */
@HiltViewModel
class MessageViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
) : ViewModel() {
    /**
     * 网络状态（用于顶部提示条）
     */
    val networkStatus: StateFlow<NetworkStatus> =
        networkMonitor.status.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NetworkStatus.Available,
        )

    /**
     * 连接状态（后续接入 IM SDK / WebSocket / MQTT 等）
     */
    private val _connectionState = MutableStateFlow(ConnectionState.Connected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    /**
     * 收取状态（后续接入：下拉同步/后台拉取/收取消息）
     */
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    /**
     * 是否正在下拉刷新
     */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /**
     * 会话列表（临时，后续从 Repository 获取）
     */
    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations.asStateFlow()

    /**
     * UI 状态（结合所有状态）
     */
    val uiState: StateFlow<MessageUiState> = combine(
        conversations,
        isRefreshing,
        networkStatus,
        connectionState,
        isSyncing,
    ) { convs, refreshing, network, connection, syncing ->
        val hasPinnedMessages = convs.any { it.isPinned }
        
        MessageUiState.Success(
            conversations = convs,
            hasPinnedMessages = hasPinnedMessages,
            isRefreshing = refreshing,
            networkStatus = network,
            connectionState = connection,
            isSyncing = syncing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MessageUiState.Loading
    )

    init {
        // TODO: 加载会话列表
        loadConversations()
    }

    /**
     * 加载会话列表
     */
    private fun loadConversations() {
        viewModelScope.launch {
            // TODO: 从 Repository 获取会话列表
            // val result = messageRepository.getConversations()
            // _conversations.value = result
            
            // 临时：模拟数据（用于测试）
            // 创建测试会话列表，确保至少有一个置顶消息，以便显示头像
            _conversations.value = listOf(
                ConversationItem(
                    id = "1",
                    name = "测试会话1",
                    avatarUrl = null,
                    lastMessage = "这是一条测试消息",
                    timestamp = System.currentTimeMillis(),
                    unreadCount = 0,
                    isGroup = false,
                    isPinned = true, // 置顶，确保显示头像
                ),
                ConversationItem(
                    id = "2",
                    name = "测试会话2",
                    avatarUrl = null,
                    lastMessage = "另一条测试消息",
                    timestamp = System.currentTimeMillis() - 3600000,
                    unreadCount = 3,
                    isGroup = false,
                    isPinned = false,
                ),
            )
        }
    }

    /**
     * 下拉刷新
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // TODO: 从 Repository 刷新会话列表
            // val result = messageRepository.refreshConversations()
            // _conversations.value = result
            _isRefreshing.value = false
        }
    }

    /**
     * 更新下拉刷新状态（由 UI 层调用）
     */
    fun updateRefreshingState(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    // 预留：供后续外部更新
    fun setConnecting() {
        _connectionState.value = ConnectionState.Connecting
    }

    fun setConnected() {
        _connectionState.value = ConnectionState.Connected
    }

    fun setDisconnected() {
        _connectionState.value = ConnectionState.Disconnected
    }

    fun setSyncing(syncing: Boolean) {
        _isSyncing.value = syncing
    }
}

enum class ConnectionState {
    Connecting,
    Connected,
    Disconnected,
}




