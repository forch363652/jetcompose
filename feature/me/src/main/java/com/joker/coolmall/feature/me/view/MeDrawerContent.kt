package com.joker.coolmall.feature.me.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.joker.coolmall.feature.me.R
import com.joker.coolmall.navigation.routes.MeRoutes

data class MeDrawerItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

/**
 * “我的”抽屉内容（图5）
 *
 * - 该组件是纯 UI：点击项时只上抛 route，不直接依赖 NavController
 */
@Composable
fun MeDrawerContent(
    modifier: Modifier = Modifier,
    onItemClick: (route: String) -> Unit,
) {
    val items = listOf(
        MeDrawerItem(MeRoutes.MOMENTS, stringResource(R.string.me_drawer_moments), Icons.Default.FavoriteBorder),
        MeDrawerItem(MeRoutes.FAVORITES, stringResource(R.string.me_drawer_favorites), Icons.Default.Star),
        MeDrawerItem(MeRoutes.STATUS, stringResource(R.string.me_drawer_status), Icons.Default.Info),
        MeDrawerItem(MeRoutes.NOTIFICATIONS, stringResource(R.string.me_drawer_notifications), Icons.Default.Notifications),
        MeDrawerItem(MeRoutes.PRIVACY, stringResource(R.string.me_drawer_privacy), Icons.Default.Lock),
        MeDrawerItem(MeRoutes.ACCOUNT_SECURITY, stringResource(R.string.me_drawer_account_security), Icons.Default.Person),
        MeDrawerItem(MeRoutes.SETTINGS, stringResource(R.string.me_drawer_settings), Icons.Default.Settings),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 48.dp, bottom = 12.dp)
    ) {
        // 头部：头像 + 昵称（占位）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // TODO: 后续替换为真实头像（Coil）
                Text(
                    text = "U",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "热心网友",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: 000000",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

        Spacer(modifier = Modifier.height(8.dp))

        items.forEachIndexed { index, item ->
            DrawerRow(
                icon = item.icon,
                title = item.title,
                onClick = { onItemClick(item.route) }
            )
            if (index != items.lastIndex) {
                Divider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                )
            }
        }
    }
}

@Composable
private fun DrawerRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


