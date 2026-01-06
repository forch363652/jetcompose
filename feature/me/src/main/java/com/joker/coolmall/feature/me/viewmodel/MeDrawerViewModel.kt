package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.UserInfoStoreRepository
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.feature.me.model.MeDrawerDestination
import com.joker.coolmall.feature.me.state.MeDrawerUiState
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
 * 我的抽屉 ViewModel
 *
 * 提供头像、昵称、ID、未读数/角标等数据
 * 使数据来源（本地缓存/网络）与 UI 解耦
 */
@HiltViewModel
class MeDrawerViewModel @Inject constructor(
    private val appState: AppState,
    private val userInfoStoreRepository: UserInfoStoreRepository,
) : ViewModel() {

    private val _selectedRoute = MutableStateFlow<String?>(null)
    private val _badgeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())

    /**
     * UI 状态（结合 AppState 的用户信息和本地状态）
     */
    val uiState: StateFlow<MeDrawerUiState> = combine(
        appState.userInfo,
        _selectedRoute,
        _badgeCounts,
    ) { user, selectedRoute, badgeCounts ->
        MeDrawerUiState(
            avatarUrl = user?.avatarUrl,
            nickname = user?.nickName ?: "热心网友",
            userId = user?.id?.toString() ?: "000000",
            destinations = MeDrawerDestination.defaultDestinations(),
            selectedRoute = selectedRoute,
            badgeCounts = badgeCounts,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MeDrawerUiState(
            destinations = MeDrawerDestination.defaultDestinations(),
        )
    )

    init {
        // 加载未读数/角标
        loadBadgeCounts()
    }

    /**
     * 加载未读数/角标
     */
    private fun loadBadgeCounts() {
        viewModelScope.launch {
            // TODO: 从 Repository 获取未读数
            // val counts = userRepository.getBadgeCounts()
            // _badgeCounts.value = counts
        }
    }

    /**
     * 更新当前选中的路由（用于高亮显示）
     */
    fun updateSelectedRoute(route: String?) {
        _selectedRoute.value = route
    }

    /**
     * 刷新用户信息和未读数
     */
    fun refresh() {
        viewModelScope.launch {
            appState.refreshUserInfo()
            loadBadgeCounts()
        }
    }
}

