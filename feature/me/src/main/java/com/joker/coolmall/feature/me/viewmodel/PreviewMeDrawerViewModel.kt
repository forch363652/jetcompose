package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.model.preview.previewUser
import com.joker.coolmall.feature.me.model.MeDrawerDestination
import com.joker.coolmall.feature.me.state.MeDrawerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * 预览用的 MeDrawerViewModel（用于 Compose Preview）
 * 
 * 不依赖 Hilt，直接使用预览数据
 * 遵循 NIA 架构模式，提供与真实 ViewModel 相同的 UI 状态
 */
class PreviewMeDrawerViewModel : ViewModel() {
    
    private val _selectedRoute = MutableStateFlow<String?>(null)
    private val _badgeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    
    /**
     * UI 状态（使用预览数据）
     */
    val uiState: StateFlow<MeDrawerUiState> = combine(
        _selectedRoute,
        _badgeCounts,
    ) { selectedRoute, badgeCounts ->
        MeDrawerUiState(
            avatarUrl = previewUser.avatarUrl,
            nickname = previewUser.nickName ?: "热心网友",
            userId = previewUser.id.toString(),
            destinations = MeDrawerDestination.defaultDestinations(),
            selectedRoute = selectedRoute,
            badgeCounts = badgeCounts,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MeDrawerUiState(
            avatarUrl = previewUser.avatarUrl,
            nickname = previewUser.nickName ?: "热心网友",
            userId = previewUser.id.toString(),
            destinations = MeDrawerDestination.defaultDestinations(),
        )
    )
    
    fun updateSelectedRoute(route: String?) {
        _selectedRoute.value = route
    }
    
    fun refresh() {
        // 预览模式不做任何操作
    }
}

