package com.joker.coolmall.feature.me.state

/**
 * 个人资料详情 UI 状态
 */
data class ProfileDetailUiState(
    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,

    /**
     * 用户头像 URL
     */
    val avatarUrl: String? = null,

    /**
     * 用户名
     */
    val userName: String? = null,

    /**
     * 三条ID
     */
    val santiaoId: String? = null,

    /**
     * 手机号（可能部分隐藏）
     */
    val phone: String? = null,

    /**
     * 是否显示手机号（用于切换显示/隐藏）
     */
    val isPhoneVisible: Boolean = false,

    /**
     * 三条ID（用于显示）
     */
    val imid: String? = null,

    /**
     * 二维码 URL
     */
    val qrCodeUrl: String? = null,

    /**
     * 性别（0-未知，1-男，2-女）
     */
    val gender: Int = 0,

    /**
     * 邮箱
     */
    val email: String? = null,

    /**
     * 所在地
     */
    val location: String? = null,

    /**
     * 错误消息（友好的用户提示）
     */
    val errorMessage: String? = null,

    /**
     * 是否可以重试
     */
    val canRetry: Boolean = false,
) {
    /**
     * 性别显示文本
     */
    val genderText: String
        get() = when (gender) {
            1 -> "男"
            2 -> "女"
            else -> "未知"
        }

    /**
     * 手机号显示文本（根据可见性决定是否隐藏）
     */
    val phoneDisplayText: String
        get() = if (phone.isNullOrBlank()) {
            ""
        } else if (isPhoneVisible) {
            phone
        } else {
            // 隐藏中间部分，例如：381****20
            if (phone.length > 7) {
                "${phone.take(3)}****${phone.takeLast(2)}"
            } else {
                "****"
            }
        }
}

