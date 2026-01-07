package com.joker.coolmall.feature.me.state

/**
 * 三条ID详情 UI 状态
 */
data class SantiaoIdDetailUiState(
    /**
     * 三条ID
     */
    val santiaoId: String? = null,

    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,

    /**
     * 错误消息
     */
    val errorMessage: String? = null,
) {
    /**
     * 显示的三条ID文本
     */
    val displayId: String
        get() = if (santiaoId.isNullOrBlank()) {
            "暂无"
        } else {
            santiaoId
        }

    /**
     * 是否可以修改（有ID才能修改）
     */
    val canModify: Boolean
        get() = !santiaoId.isNullOrBlank()
}

