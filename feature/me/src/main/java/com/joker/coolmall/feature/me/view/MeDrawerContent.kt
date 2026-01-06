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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.core.designsystem.theme.CommonIcon
import com.joker.coolmall.feature.me.R
import com.joker.coolmall.feature.me.model.MeDrawerDestination
import com.joker.coolmall.feature.me.state.MeDrawerUiState

/**
 * “我的”抽屉内容（参考 NIA 的架构模式）
 *
 * - 使用 UiState 模式，数据与 UI 解耦
 * - 支持 LazyColumn 长列表
 * - 使用设计系统的 icon 和语义色
 * - 适配窗口内边距（windowInsetsPadding）
 * - 提供 contentDescription 满足无障碍
 */
@Composable
fun MeDrawerContent(
    uiState: MeDrawerUiState,
    modifier: Modifier = Modifier,
    onItemClick: (route: String) -> Unit,
) {
    val destinations = uiState.updateSelectedDestinations()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars) // 适配状态栏
            .padding(bottom = 12.dp),
    ) {
        // 头部：头像 + 昵称
        item {
            DrawerHeader(
                avatarUrl = uiState.avatarUrl,
                nickname = uiState.nickname,
                userId = uiState.userId,
                onHeaderClick = { onItemClick(com.joker.coolmall.navigation.routes.MeRoutes.PROFILE_DETAIL) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
        }

        // 抽屉项列表
        items(
            items = destinations,
            key = { it.route }
        ) { destination ->
            DrawerItem(
                destination = destination,
                badgeCount = uiState.badgeCounts[destination.route] ?: 0,
                onClick = { onItemClick(destination.route) },
                modifier = Modifier.fillMaxWidth()
            )

            // 分隔线（最后一项不显示）
            if (destination != destinations.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                )
            }
        }
    }
}

/**
 * 抽屉头部（头像 + 昵称 + ID）
 */
@Composable
private fun DrawerHeader(
    avatarUrl: String?,
    nickname: String,
    userId: String,
    onHeaderClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 头像（可点击）
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onHeaderClick),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl != null && avatarUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.avatar_default) // 加载中显示默认头像
                        .error(R.drawable.avatar_default) // 加载失败显示默认头像
                        .build(),
                    contentDescription = stringResource(R.string.me_drawer_avatar_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 无头像 URL：显示默认头像
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.avatar_default)
                        .build(),
                    contentDescription = stringResource(R.string.me_drawer_avatar_content_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 昵称和 ID（可点击）
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onHeaderClick),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
                Text(
                    text = nickname,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.me_drawer_user_id_format, userId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

        // 二维码图标（独立点击，不触发头部点击）
        IconButton(
            onClick = {
                // TODO: 打开二维码页面
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_qrcode),
                contentDescription = stringResource(R.string.me_drawer_qrcode_content_description),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }

        // 箭头图标（点击触发头部点击）
        IconButton(
            onClick = onHeaderClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = stringResource(R.string.me_drawer_arrow_content_description),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 抽屉项（单个菜单项）
 */
@Composable
private fun DrawerItem(
    destination: MeDrawerDestination,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .then(
                if (destination.selected) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.small
                    )
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 图标（优先使用 drawable 资源，否则使用 ImageVector）
        if (destination.iconResId != null) {
            Icon(
                painter = painterResource(id = destination.iconResId),
                contentDescription = stringResource(destination.labelTextId), // 无障碍支持
                modifier = Modifier.size(20.dp),
                tint = if (destination.selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        } else if (destination.icon != null) {
            CommonIcon(
                imageVector = destination.icon,
                contentDescription = stringResource(destination.labelTextId), // 无障碍支持
                modifier = Modifier.size(20.dp),
                tint = if (destination.selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        // 标签文本
        Text(
            text = stringResource(destination.labelTextId),
            style = MaterialTheme.typography.bodyMedium,
            color = if (destination.selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.weight(1f)
        )

        // 角标（未读数）
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.coerceAtMost(99).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
    }
}

// ==================== Preview ====================

@Preview(name = "Light Theme", showBackground = true)
@Preview(name = "Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MeDrawerContentPreview() {
    AppTheme {
        MeDrawerContent(
            uiState = MeDrawerUiState(
                nickname = "热心网友",
                userId = "123456",
                destinations = MeDrawerDestination.defaultDestinations(),
                badgeCounts = mapOf(
                    MeDrawerDestination.defaultDestinations()[3].route to 5, // Notifications
                )
            ),
            onItemClick = {}
        )
    }
}

@Preview(name = "Selected Item", showBackground = true)
@Composable
private fun MeDrawerContentSelectedPreview() {
    AppTheme {
        MeDrawerContent(
            uiState = MeDrawerUiState(
                nickname = "热心网友",
                userId = "123456",
                destinations = MeDrawerDestination.defaultDestinations(),
                selectedRoute = MeDrawerDestination.defaultDestinations()[0].route, // Moments selected
                badgeCounts = mapOf(
                    MeDrawerDestination.defaultDestinations()[3].route to 5,
                )
            ),
            onItemClick = {}
        )
    }
}
