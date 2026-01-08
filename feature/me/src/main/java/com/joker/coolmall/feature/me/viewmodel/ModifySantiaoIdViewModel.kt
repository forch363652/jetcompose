package com.joker.coolmall.feature.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.feature.me.state.ModifySantiaoIdUiState
import com.joker.coolmall.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 修改三条ID ViewModel
 */
@HiltViewModel
class ModifySantiaoIdViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModifySantiaoIdUiState())
    val uiState: StateFlow<ModifySantiaoIdUiState> = _uiState.asStateFlow()

    /**
     * 初始化（从参数获取当前ID）
     */
    fun initialize(currentId: String) {
        _uiState.update { state ->
            state.copy(
                currentId = currentId,
                inputText = currentId,
                showClearButton = currentId.isNotEmpty(),
                counterText = "${currentId.length} / ${ModifySantiaoIdUiState.MAX_COUNT} 字"
            )
        }
    }

    /**
     * 更新输入文本
     */
    fun updateInputText(text: String) {
        val trimmedText = text.take(ModifySantiaoIdUiState.MAX_COUNT)
        _uiState.update { state ->
            state.copy(
                inputText = trimmedText,
                showClearButton = trimmedText.isNotEmpty(),
                counterText = "${trimmedText.length} / ${ModifySantiaoIdUiState.MAX_COUNT} 字",
                errorMessage = null
            )
        }
    }

    /**
     * 清除输入
     */
    fun clearInput() {
        _uiState.update { state ->
            state.copy(
                inputText = "",
                showClearButton = false,
                counterText = "0 / ${ModifySantiaoIdUiState.MAX_COUNT} 字",
                errorMessage = null
            )
        }
    }

    /**
     * 保存ID
     */
    fun saveId(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentText = _uiState.value.inputText.trim()

        // 验证
        if (currentText.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "ID不能为空") }
            onError("ID不能为空")
            return
        }

        // 验证格式：必须同时包含字母和数字
        val idPattern = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$"
        if (!currentText.matches(Regex(idPattern))) {
            _uiState.update { it.copy(errorMessage = "ID必须同时包含字母和数字") }
            onError("ID必须同时包含字母和数字")
            return
        }

        // 检查是否修改
        if (currentText == _uiState.value.currentId) {
            _uiState.update { it.copy(errorMessage = "ID未修改") }
            onError("ID未修改")
            return
        }

        // 保存
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // 调用 UserRepository 更新ID（乐观更新模式）
                // 即使没有网络，也会先保存到本地，然后后台尝试同步
                val result = userRepository.updateUserId(currentText)
                
                if (result is Result.Success) {
                    // 更新成功（本地已保存）
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            currentId = currentText, // 更新当前ID
                            errorMessage = null
                        )
                    }
                    onSuccess()
                } else {
                    // 更新失败（通常是本地没有用户信息）
                    val error = (result as? Result.Error)?.exception?.message ?: "保存失败"
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            errorMessage = error
                        )
                    }
                    onError(error)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "保存失败"
                    )
                }
                onError(e.message ?: "保存失败")
            }
        }
    }
}

