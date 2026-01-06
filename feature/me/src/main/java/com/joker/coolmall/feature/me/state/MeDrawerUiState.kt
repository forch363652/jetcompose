package com.joker.coolmall.feature.me.state

import com.joker.coolmall.feature.me.model.MeDrawerDestination

/**
 * 我的抽屉 UI 状态（参考 NIA 的 UiState 模式）
 *
 * 将数据来源（本地缓存/网络）与 UI 解耦
 */
data class MeDrawerUiState(
    /**
     * 用户头像 URL（null 表示使用占位）
     */
    val avatarUrl: String? = null,

    /**
     * 用户昵称
     */
    val nickname: String = "",

    /**
     * 用户 ID（显示格式：ID: xxxxxx）
     */
    val userId: String = "",

    /**
     * 抽屉目的地列表（包含选中状态）
     */
    val destinations: List<MeDrawerDestination> = MeDrawerDestination.defaultDestinations(),

    /**
     * 当前选中的路由（用于高亮显示）
     */
    val selectedRoute: String? = null,

    /**
     * 未读数/角标映射（route -> count，0 表示不显示）
     */
    val badgeCounts: Map<String, Int> = emptyMap(),

    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,
) {
    /**
     * 根据当前选中路由更新 destinations 的 selected 状态
     */
    fun updateSelectedDestinations(): List<MeDrawerDestination> {
        return destinations.map { destination ->
            destination.copy(selected = destination.route == selectedRoute)
        }
    }
}

