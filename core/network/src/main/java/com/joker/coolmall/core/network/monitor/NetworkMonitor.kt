package com.joker.coolmall.core.network.monitor

import kotlinx.coroutines.flow.Flow

/**
 * 网络状态监听（基于系统 ConnectivityManager）
 *
 * 说明：
 * - 仅用于 UI 层提示“网络可用/不可用/不佳”等状态
 * - 具体业务重试/降级策略仍由各业务模块自行处理
 */
interface NetworkMonitor {
    val status: Flow<NetworkStatus>
}

sealed interface NetworkStatus {
    data object Available : NetworkStatus
    data object Unavailable : NetworkStatus
}




