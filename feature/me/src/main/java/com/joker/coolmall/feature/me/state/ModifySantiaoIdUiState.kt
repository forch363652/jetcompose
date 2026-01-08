package com.joker.coolmall.feature.me.state

/**
 * 修改三条ID UI 状态
 */
data class ModifySantiaoIdUiState(
    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,

    /**
     * 当前ID
     */
    val currentId: String = "",

    /**
     * 输入框文本
     */
    val inputText: String = "",

    /**
     * 是否显示清除按钮
     */
    val showClearButton: Boolean = false,

    /**
     * 字符计数（格式：x / 18 字）
     */
    val counterText: String = "0 / 18 字",

    /**
     * 错误消息
     */
    val errorMessage: String? = null,

    /**
     * 是否正在保存
     */
    val isSaving: Boolean = false,
) {
    companion object {
        /**
         * 最大字符数
         */
        const val MAX_COUNT = 18
    }
}

