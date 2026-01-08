package com.joker.coolmall.feature.me.view

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.core.model.preview.previewUser
import com.joker.coolmall.feature.me.R
import com.joker.coolmall.feature.me.mapper.UserMapper.toUiState
import com.joker.coolmall.feature.me.viewmodel.ProfileDetailViewModel

/**
 * 个人资料详情页面入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailRoute(
    onBackClick: () -> Unit,
    viewModel: ProfileDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onTogglePhoneVisibility = { viewModel.togglePhoneVisibility() },
        onRetry = { viewModel.retry() },
        onSantiaoIdClick = { santiaoId ->
            viewModel.navigateToSantiaoIdDetail(santiaoId)
        },
        onQRCodeClick = {
            viewModel.navigateToQRCode(
                qrCodeUrl = uiState.qrCodeUrl,
                userName = uiState.userName,
                avatarUrl = uiState.avatarUrl
            )
        },
    )
}

/**
 * 个人资料详情页面
 */
@Composable
private fun ProfileDetailScreen(
    uiState: com.joker.coolmall.feature.me.state.ProfileDetailUiState,
    onBackClick: () -> Unit,
    onTogglePhoneVisibility: () -> Unit,
    onRetry: () -> Unit = {},
    onSantiaoIdClick: (String) -> Unit = {},
    onQRCodeClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 背景图片区域（白色卡片以上的部分）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // 背景图片高度
            ) {
                // 背景图片
                Image(
                    painter = painterResource(id = R.drawable.bg_profile),
                    contentDescription = "profile_background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 返回按钮（覆盖在背景图片左上角）
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .statusBarsPadding()
                ) {
                    Icon(
                        painter = painterResource(id = com.joker.coolmall.core.designsystem.R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.me_profile_back),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 白色卡片（向上偏移，与背景图片重叠，左右有间距）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset(y = (-24).dp) // 向上偏移，与背景图片重叠
                    .padding(horizontal = 16.dp), // 左右间距
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        // 加载中：显示加载指示器
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.errorMessage != null && uiState.canRetry -> {
                        // 错误状态：显示错误信息和重试按钮
                        // 如果之前有数据，保留显示；如果没有数据，显示错误提示
                        if (uiState.userName == null && uiState.avatarUrl == null) {
                            // 没有数据，显示完整错误页面
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Text(
                                        text = uiState.errorMessage,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    TextButton(onClick = onRetry) {
                                        Text("重试")
                                    }
                                }
                            }
                        } else {
                            // 有数据，在顶部显示错误提示和重试按钮
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 错误提示栏
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = uiState.errorMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(onClick = onRetry) {
                                            Text("重试")
                                        }
                                    }
                                }
                                // 显示之前的数据
                                ProfileContent(
                                    uiState = uiState,
                                    onTogglePhoneVisibility = onTogglePhoneVisibility,
                                    onSantiaoIdClick = onSantiaoIdClick,
                                    onQRCodeClick = onQRCodeClick,
                                )
                            }
                        }
                    }
                    else -> {
                        // 成功状态：显示内容
                        ProfileContent(
                            uiState = uiState,
                            onTogglePhoneVisibility = onTogglePhoneVisibility,
                            onSantiaoIdClick = onSantiaoIdClick,
                            onQRCodeClick = onQRCodeClick,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 个人资料内容
 */
@Composable
private fun ProfileContent(
    uiState: com.joker.coolmall.feature.me.state.ProfileDetailUiState,
    onTogglePhoneVisibility: () -> Unit,
    onSantiaoIdClick: (String) -> Unit = {},
    onQRCodeClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp) // 移除spacing，手动控制间距
    ) {
        // 头像和用户ID
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier.size(70.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.avatarUrl ?: R.drawable.avatar_default)
                        .crossfade(true)
                        .placeholder(R.drawable.avatar_default)
                        .error(R.drawable.avatar_default)
                        .build(),
                    contentDescription = stringResource(R.string.me_drawer_avatar_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
                // 相机图标（编辑头像）
                Icon(
                    painter = painterResource(id = R.drawable.ic_new_mine_setting),
                    contentDescription = "edit_avatar",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 用户ID
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.santiaoId ?: "000000",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "edit_id",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // 手机号
        ProfileItem(
            label = stringResource(R.string.me_profile_phone),
            value = uiState.phoneDisplayText,
            trailingIcon = {
                IconButton(onClick = onTogglePhoneVisibility) {
                    Icon(
                        painter = painterResource(
                            id = if (uiState.isPhoneVisible) {
                                R.drawable.ic_invisible
                            } else {
                                R.drawable.ic_visible
                            }
                        ),
                        contentDescription = if (uiState.isPhoneVisible) "hide_phone" else "show_phone",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // 下划线分隔
        HorizontalDivider(
            modifier = Modifier.padding(start = 60.dp), // 从左侧60dp开始，对齐图标位置
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            thickness = 1.dp
        )

        // 三条ID
        ProfileItem(
            label = stringResource(R.string.me_profile_santiao_id),
            value = uiState.imid ?: uiState.santiaoId ?: "--",
            onClick = {
                val santiaoId = uiState.imid ?: uiState.santiaoId
                if (!santiaoId.isNullOrBlank()) {
                    onSantiaoIdClick(santiaoId)
                }
            }
        )

        // 下划线分隔
        HorizontalDivider(
            modifier = Modifier.padding(start = 60.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            thickness = 1.dp
        )

        // 我的二维码
        ProfileItem(
            label = stringResource(R.string.me_profile_qr_code),
            value = "",
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_qrcode),
                    contentDescription = "qr_code",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            onClick = onQRCodeClick
        )

        // 性别
        ProfileItem(
            label = stringResource(R.string.me_profile_gender),
            value = uiState.genderText,
            onClick = { /* TODO: 编辑性别 */ }
        )

        // 邮箱
        ProfileItem(
            label = stringResource(R.string.me_profile_email),
            value = uiState.email ?: "",
            onClick = { /* TODO: 编辑邮箱 */ }
        )

        // 所在地
        ProfileItem(
            label = stringResource(R.string.me_profile_location),
            value = uiState.location ?: "",
            onClick = { /* TODO: 编辑所在地 */ }
        )
    }
}

/**
 * 个人资料项
 */
@Composable
private fun ProfileItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 10.dp, vertical = 16.dp), // 调整padding，参考XML的dp_10和dp_55高度
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            trailingIcon?.invoke()

            if (onClick != null) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileDetailScreenPreview() {
    AppTheme {
        // 使用 UserMapper 进行状态转换
        val previewUiState = previewUser.toUiState(isPhoneVisible = false)
        
        ProfileDetailScreen(
            uiState = previewUiState,
            onBackClick = {},
            onTogglePhoneVisibility = {},
            onRetry = {},
            onSantiaoIdClick = {},
        )
    }
}

