package com.joker.coolmall.feature.me.state

import android.graphics.Bitmap

/**
 * 二维码页面 UI 状态
 */
data class QRCodeUiState(
    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,

    /**
     * 二维码 Bitmap
     */
    val qrCodeBitmap: Bitmap? = null,

    /**
     * 二维码字符串（用于生成二维码）
     */
    val qrCodeString: String? = null,

    /**
     * 用户头像 URL
     */
    val avatarUrl: String? = null,

    /**
     * 用户名
     */
    val userName: String? = null,

    /**
     * 错误消息
     */
    val errorMessage: String? = null,

    /**
     * 是否正在保存到相册
     */
    val isSaving: Boolean = false,

    /**
     * 保存结果消息
     */
    val saveMessage: String? = null,
)

