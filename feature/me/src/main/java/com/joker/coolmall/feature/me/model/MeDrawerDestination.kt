package com.joker.coolmall.feature.me.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.joker.coolmall.feature.me.R
import com.joker.coolmall.navigation.routes.MeRoutes

/**
 * 我的抽屉目的地数据模型（类似 NIA 的 TopLevelDestination）
 *
 * @property route 路由
 * @property icon 图标（ImageVector）
 * @property labelTextId 标签文本资源 ID
 * @property selected 是否选中（用于高亮显示）
 */
data class MeDrawerDestination(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelTextId: Int,
    val selected: Boolean = false,
) {
    companion object {
        /**
         * 获取所有抽屉目的地列表（默认未选中）
         * 从上层传入，避免在 UI 组件内硬编码
         */
        fun defaultDestinations(): List<MeDrawerDestination> = listOf(
            MeDrawerDestination(
                route = MeRoutes.MOMENTS,
                icon = Icons.Default.FavoriteBorder,
                labelTextId = R.string.me_drawer_moments
            ),
            MeDrawerDestination(
                route = MeRoutes.FAVORITES,
                icon = Icons.Default.Star,
                labelTextId = R.string.me_drawer_favorites
            ),
            MeDrawerDestination(
                route = MeRoutes.STATUS,
                icon = Icons.Default.Info,
                labelTextId = R.string.me_drawer_status
            ),
            MeDrawerDestination(
                route = MeRoutes.NOTIFICATIONS,
                icon = Icons.Default.Notifications,
                labelTextId = R.string.me_drawer_notifications
            ),
            MeDrawerDestination(
                route = MeRoutes.PRIVACY,
                icon = Icons.Default.Lock,
                labelTextId = R.string.me_drawer_privacy
            ),
            MeDrawerDestination(
                route = MeRoutes.ACCOUNT_SECURITY,
                icon = Icons.Default.Person,
                labelTextId = R.string.me_drawer_account_security
            ),
            MeDrawerDestination(
                route = MeRoutes.SETTINGS,
                icon = Icons.Default.Settings,
                labelTextId = R.string.me_drawer_settings
            ),
        )
    }
}

