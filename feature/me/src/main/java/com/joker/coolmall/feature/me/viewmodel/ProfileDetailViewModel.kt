package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.common.base.viewmodel.BaseViewModel
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.core.data.usecase.GetUserProfileUseCase
import com.joker.coolmall.feature.me.mapper.UserMapper.toUiState
import com.joker.coolmall.feature.me.state.ProfileDetailUiState
import com.joker.coolmall.navigation.AppNavigator
import com.joker.coolmall.navigation.routes.MeRoutes
import com.joker.coolmall.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 个人资料详情 ViewModel
 * 
 * 优化说明：
 * - 使用 UseCase 处理数据优先级逻辑，ViewModel 只负责 UI 状态管理
 * - 使用 Result 类型统一处理成功/失败状态
 * - 使用 UserMapper 进行状态转换，避免字段复制
 * - 优化加载状态：进入时先 isLoading = true，成功或失败后置 false
 * - 异常时保留上一次有效数据而不是全部清空
 * - 提供友好的错误提示和重试机制
 * - 继承 BaseViewModel 以获得导航功能
 */
@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    navigator: AppNavigator,
    appState: AppState,
) : BaseViewModel(navigator, appState) {

    private val _uiState = MutableStateFlow(ProfileDetailUiState(isLoading = true))
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * 加载用户资料
     * 
     * 使用 Result 类型统一处理状态，避免 try/catch 散落
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            // 设置加载状态
            _uiState.update { it.copy(isLoading = true, errorMessage = null, canRetry = false) }

            when (val result = getUserProfileUseCase()) {
                is Result.Loading -> {
                    // UseCase 内部已处理，这里通常不会到达
                    _uiState.update { it.copy(isLoading = true) }
                }

                is Result.Success -> {
                    val user = result.data
                    // 使用 UserMapper 进行状态转换
                    val newUiState = user.toUiState(
                        isPhoneVisible = _uiState.value.isPhoneVisible // 保留手机号可见性状态
                    )
                    _uiState.update {
                        newUiState.copy(
                            isLoading = false,
                            errorMessage = null,
                            canRetry = false,
                        )
                    }
                }

                is Result.Error -> {
                    val exception = result.exception
                    // 异常时保留上一次有效数据，只更新错误信息
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(exception),
                            canRetry = true, // 允许重试
                        )
                    }
                }
            }
        }
    }

    /**
     * 获取友好的错误提示
     * 
     * @param exception 异常信息
     * @return 友好的错误提示文本
     */
    private fun getErrorMessage(exception: Throwable?): String {
        return when {
            exception?.message.isNullOrBlank() -> "加载用户信息失败，请稍后重试"
            else -> exception?.message ?: "未知错误"
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
        loadUserProfile()
    }

    /**
     * 重试加载用户资料
     */
    fun retry() {
        loadUserProfile()
    }

    /**
     * 导航到三条ID详情页面
     * 
     * @param santiaoId 三条ID
     */
    fun navigateToSantiaoIdDetail(santiaoId: String) {
        toPage(MeRoutes.SANTIAO_ID_DETAIL, mapOf("santiaoId" to santiaoId))
    }
}

