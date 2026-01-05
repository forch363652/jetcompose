package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.network.monitor.NetworkMonitor
import com.joker.coolmall.core.network.monitor.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

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
     * 连接状态（后续接入你的 IM SDK / WebSocket / MQTT 等）
     */
    private val _connectionState = MutableStateFlow(ConnectionState.Connected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    /**
     * 收取状态（后续接入：下拉同步/后台拉取/收取消息）
     */
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

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




