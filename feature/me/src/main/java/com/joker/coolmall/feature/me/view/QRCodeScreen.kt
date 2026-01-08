package com.joker.coolmall.feature.me.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joker.coolmall.core.designsystem.R
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.feature.me.R as MeR
import com.joker.coolmall.feature.me.state.QRCodeUiState
import com.joker.coolmall.feature.me.viewmodel.QRCodeViewModel
import com.joker.coolmall.core.util.toast.ToastUtils

/**
 * 二维码页面入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeRoute(
    onBackClick: () -> Unit,
    viewModel: QRCodeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 显示保存结果消息
    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let {
            ToastUtils.show(it)
            viewModel.clearSaveMessage()
        }
    }

    QRCodeScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSaveClick = { viewModel.saveQRCodeToAlbum() },
    )
}

/**
 * 二维码页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRCodeScreen(
    uiState: QRCodeUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部导航栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = stringResource(MeR.string.me_profile_back),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 标题
                Text(
                    text = stringResource(MeR.string.me_qrcode_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // 占位（保持标题居中）
                Spacer(modifier = Modifier.size(35.dp))
            }

            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        // 加载中
                        CircularProgressIndicator()
                    }
                    uiState.errorMessage != null -> {
                        // 错误状态
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    uiState.qrCodeBitmap != null -> {
                        // 显示二维码卡片
                        QRCodeCard(
                            qrCodeBitmap = uiState.qrCodeBitmap,
                            avatarUrl = uiState.avatarUrl,
                            userName = uiState.userName,
                            onSaveClick = onSaveClick,
                            isSaving = uiState.isSaving
                        )
                    }
                }
            }
        }
    }
}

/**
 * 二维码卡片
 */
@Composable
private fun QRCodeCard(
    qrCodeBitmap: android.graphics.Bitmap,
    avatarUrl: String?,
    userName: String?,
    onSaveClick: () -> Unit,
    isSaving: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 二维码图片
            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = stringResource(MeR.string.me_qrcode_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentScale = ContentScale.Fit
            )

            // 用户信息（头像 + 用户名）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl ?: MeR.drawable.avatar_default)
                        .crossfade(true)
                        .placeholder(MeR.drawable.avatar_default)
                        .error(MeR.drawable.avatar_default)
                        .build(),
                    contentDescription = stringResource(MeR.string.me_drawer_avatar_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.size(10.dp))

                // 用户名
                Text(
                    text = userName ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 性别图标（如果有）
                // TODO: 根据用户性别显示图标
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 保存按钮
            Text(
                text = stringResource(MeR.string.me_qrcode_save),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(enabled = !isSaving, onClick = onSaveClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 提示文本
            Text(
                text = stringResource(MeR.string.me_qrcode_tip),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QRCodeScreenPreview() {
    AppTheme {
        QRCodeScreen(
            uiState = QRCodeUiState(
                isLoading = false,
                qrCodeBitmap = android.graphics.Bitmap.createBitmap(800, 800, android.graphics.Bitmap.Config.ARGB_8888),
                userName = "测试用户",
                avatarUrl = null
            ),
            onBackClick = {},
            onSaveClick = {},
        )
    }
}

