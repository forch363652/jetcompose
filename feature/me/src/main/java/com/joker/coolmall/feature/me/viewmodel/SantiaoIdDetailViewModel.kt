package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.common.base.viewmodel.BaseViewModel
import com.joker.coolmall.core.data.state.AppState
import com.joker.coolmall.core.data.usecase.GetUserProfileUseCase
import com.joker.coolmall.feature.me.state.SantiaoIdDetailUiState
import com.joker.coolmall.navigation.AppNavigator
import com.joker.coolmall.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 三条ID详情 ViewModel
 * 
 * 职责：
 * - 加载并显示三条ID信息
 * - 处理修改ID的导航逻辑
 */
@HiltViewModel
class SantiaoIdDetailViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    navigator: AppNavigator,
    appState: AppState,
) : BaseViewModel(navigator, appState) {

    private val _uiState = MutableStateFlow(SantiaoIdDetailUiState(isLoading = true))
    val uiState: StateFlow<SantiaoIdDetailUiState> = _uiState.asStateFlow()

    init {
        loadSantiaoId()
    }

    /**
     * 加载三条ID
     */
    private fun loadSantiaoId() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getUserProfileUseCase()) {
                is Result.Success -> {
                    val user = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            santiaoId = user.unionid.ifBlank { null },
                            errorMessage = null,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message,
                        )
                    }
                }

                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    /**
     * 导航到修改ID页面
     * 
     * @param santiaoId 当前的三条ID
     */
    fun navigateToModifyId(santiaoId: String) {
        // TODO: 实现修改ID页面的导航
        // toPage(MeRoutes.MODIFY_SANTIAO_ID, mapOf("santiaoId" to santiaoId))
    }
}

