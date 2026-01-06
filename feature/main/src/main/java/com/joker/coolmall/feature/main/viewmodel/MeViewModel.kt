package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.common.base.viewmodel.BaseViewModel
import com.joker.coolmall.core.data.repository.FootprintRepository
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.core.model.entity.Footprint
import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.navigation.AppNavigator
import com.joker.coolmall.navigation.routes.AuthRoutes
// import com.joker.coolmall.navigation.routes.CsRoutes // 已删除客服模块
// import com.joker.coolmall.navigation.routes.GoodsRoutes // 已删除商品模块
// import com.joker.coolmall.navigation.routes.OrderRoutes // 已删除订单模块
// import com.joker.coolmall.navigation.routes.UserRoutes // 已删除用户模块
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 我的页面视图模型
 */
@HiltViewModel
class MeViewModel @Inject constructor(
    navigator: AppNavigator,
    appState: AppState,
    private val footprintRepository: FootprintRepository
) : BaseViewModel(
    navigator = navigator,
    appState = appState
) {

    // 用户登录状态
    val isLoggedIn: StateFlow<Boolean> = appState.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // 用户信息
    val userInfo: StateFlow<User?> = appState.userInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 最新的8个足迹记录
    val recentFootprints: StateFlow<List<Footprint>> = footprintRepository.getRecentFootprints(8)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // 如果已登录但没有用户信息，则刷新用户信息
        viewModelScope.launch {
            if (isLoggedIn.value && userInfo.value == null) {
                appState.refreshUserInfo()
            }
        }
    }

    /**
     * 跳转到订单列表（已废弃 - 订单模块已删除）
     */
    @Deprecated("订单模块已删除")
    fun toOrderListPage() {
        // TODO: 社交应用不再需要订单列表
        // super.toPage(OrderRoutes.LIST)
    }

    /**
     * 跳转到指定状态的订单列表（已废弃 - 订单模块已删除）
     *
     * @param tabIndex 订单标签索引：0-全部，1-待付款，2-待发货，3-待收货，4-售后，5-待评价，6-已完成
     */
    @Deprecated("订单模块已删除")
    fun toOrderListPage(tabIndex: Int) {
        // TODO: 社交应用不再需要订单列表
        // val route = "${OrderRoutes.LIST}?tab=$tabIndex"
        // super.toPage(route)
    }

    /**
     * 用户资料点击
     */
    fun onHeadClick() {
        toLoginPage() // 暂时跳转到登录页，后续可改为个人资料页
    }

    /**
     * 跳转到商品详情（已废弃 - 商品模块已删除）
     */
    @Deprecated("商品模块已删除")
    fun toGoodsDetailPage(goodsId: Long) {
        // TODO: 社交应用不再需要商品详情
        // super.toPage(GoodsRoutes.DETAIL, goodsId)
    }

    /**
     * 跳转到用户足迹（已废弃 - 用户模块已删除）
     */
    @Deprecated("用户模块已删除")
    fun toUserFootprintPage() {
        // TODO: 社交应用不再需要用户足迹
        // super.toPage(UserRoutes.FOOTPRINT)
    }

    /**
     * 跳转到收货地址列表（已废弃 - 用户模块已删除）
     */
    @Deprecated("用户模块已删除")
    fun toAddressListPage() {
        // TODO: 社交应用不再需要收货地址
        // super.toPage(UserRoutes.ADDRESS_LIST)
    }

    /**
     * 跳转到用户资料页面（已废弃 - 用户模块已删除）
     */
    @Deprecated("用户模块已删除")
    fun toProfilePage() {
        // TODO: 社交应用的个人资料功能需要重新实现
        // val avatarUrl = userInfo.value?.avatarUrl
        // val route = if (avatarUrl?.isNotEmpty() == true) {
        //     "${UserRoutes.PROFILE}?avatar_url=${java.net.URLEncoder.encode(avatarUrl, "UTF-8")}"
        // } else {
        //     UserRoutes.PROFILE
        // }
        // super.toPage(route)
    }

    /**
     * 跳转到登录页面
     */
    fun toLoginPage() {
        super.toPage(AuthRoutes.HOME)
    }

    /**
     * 跳转到客服聊天页面（已废弃 - 客服模块已删除）
     */
    @Deprecated("客服模块已删除")
    fun toChatPage() {
        // TODO: 社交应用不再需要客服聊天
        // super.toPage(CsRoutes.CHAT)
    }
}
