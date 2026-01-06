package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.UserInfoStoreRepository
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.feature.me.state.ProfileDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 个人资料详情 ViewModel
 */
@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val appState: AppState,
    private val userInfoStoreRepository: UserInfoStoreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailUiState(isLoading = true))
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * 加载用户资料
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = appState.userInfo.value ?: userInfoStoreRepository.getUserInfo()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        avatarUrl = user?.avatarUrl,
                        userName = user?.nickName,
                        santiaoId = user?.id?.toString(),
                        phone = user?.phone,
                        gender = user?.gender ?: 0,
                        email = null, // TODO: 从服务器获取邮箱
                        location = null, // TODO: 从服务器获取所在地
                        errorMessage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = e.message,
                    )
                }
            }
        }
    }

    /**
     * 切换手机号显示/隐藏
     */
    fun togglePhoneVisibility() {
        _uiState.update { state ->
            state.copy(isPhoneVisible = !state.isPhoneVisible)
        }
    }

    /**
     * 刷新用户资料
     */
    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadUserProfile()
    }
}

