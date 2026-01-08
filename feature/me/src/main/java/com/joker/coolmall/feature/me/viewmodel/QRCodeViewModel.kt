package com.joker.coolmall.feature.me.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.UserRepository
import com.joker.coolmall.core.data.usecase.GetUserProfileUseCase
import com.joker.coolmall.feature.me.state.QRCodeUiState
import com.joker.coolmall.navigation.routes.MeRoutes
import com.joker.coolmall.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * 二维码页面 ViewModel
 */
@HiltViewModel
class QRCodeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRCodeUiState(isLoading = true))
    val uiState: StateFlow<QRCodeUiState> = _uiState.asStateFlow()

    // 从导航参数获取数据（如果有）
    private val qrCodeUrl: String? = savedStateHandle.get<String>(MeRoutes.QR_CODE_URL_ARG)
    private val userName: String? = savedStateHandle.get<String>(MeRoutes.QR_CODE_USER_NAME_ARG)
    private val avatarUrl: String? = savedStateHandle.get<String>(MeRoutes.QR_CODE_AVATAR_ARG)

    init {
        loadQRCode()
    }

    /**
     * 加载二维码
     */
    private fun loadQRCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 如果导航参数中有数据，直接使用
            if (qrCodeUrl != null && userName != null) {
                generateQRCode(qrCodeUrl, userName, avatarUrl)
                return@launch
            }

            // 否则从 UseCase 获取用户信息
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> {
                    val user = result.data
                    val qrString = user.qrCodeUrl ?: user.unionid ?: user.id.toString()
                    generateQRCode(qrString, user.nickName ?: "", user.avatarUrl)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception?.message ?: "加载二维码失败"
                        )
                    }
                }
                else -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "加载中..."
                        )
                    }
                }
            }
        }
    }

    /**
     * 生成二维码 Bitmap
     */
    private suspend fun generateQRCode(
        qrString: String,
        userName: String,
        avatarUrl: String?
    ) = withContext(Dispatchers.Default) {
        try {
            val bitmap = QRCodeGenerator.createQRCode(qrString, bgColor = Color.WHITE)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    qrCodeBitmap = bitmap,
                    qrCodeString = qrString,
                    userName = userName,
                    avatarUrl = avatarUrl,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "生成二维码失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 保存二维码到相册
     */
    fun saveQRCodeToAlbum() {
        val bitmap = _uiState.value.qrCodeBitmap ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveMessage = null) }

            val result = withContext(Dispatchers.IO) {
                saveBitmapToAlbum(bitmap)
            }

            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveMessage = if (result) "二维码已保存到相册" else "保存失败，请重试"
                )
            }
        }
    }

    /**
     * 保存 Bitmap 到相册
     */
    private suspend fun saveBitmapToAlbum(bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
        val fileName = "qr_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/SanTiaoTalk"
            )
        }

        val resolver = context.contentResolver
        var uri: android.net.Uri? = null

        return@withContext try {
            uri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let { targetUri ->
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            } ?: false
        } catch (e: IOException) {
            uri?.let { resolver.delete(it, null, null) }
            false
        }
    }

    /**
     * 清除保存消息
     */
    fun clearSaveMessage() {
        _uiState.update { it.copy(saveMessage = null) }
    }
}

/**
 * 二维码生成工具类
 * 使用 ZXing 库生成二维码
 */
object QRCodeGenerator {
    /**
     * 创建二维码 Bitmap
     *
     * @param content 二维码内容
     * @param width 二维码宽度（像素）
     * @param height 二维码高度（像素）
     * @param bgColor 背景颜色
     * @return 二维码 Bitmap
     */
    fun createQRCode(
        content: String,
        width: Int = 800,
        height: Int = 800,
        bgColor: Int = Color.WHITE
    ): Bitmap {
        return try {
            // 使用 ZXing 生成二维码
            val writer = com.google.zxing.qrcode.QRCodeWriter()
            val bitMatrix = writer.encode(
                content,
                com.google.zxing.BarcodeFormat.QR_CODE,
                width,
                height
            )

            // 将 BitMatrix 转换为 Bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) Color.BLACK else bgColor
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            // 如果生成失败，返回一个占位符
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(bgColor)
            bitmap
        }
    }
}

