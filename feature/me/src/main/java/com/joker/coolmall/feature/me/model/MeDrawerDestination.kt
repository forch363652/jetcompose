package com.joker.coolmall.feature.me.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.joker.coolmall.feature.me.R
import com.joker.coolmall.navigation.routes.MeRoutes

/**
 * 我的抽屉目的地数据模型（类似 NIA 的 TopLevelDestination）
 *
 * @property route 路由
 * @property icon 图标（ImageVector，用于没有 drawable 资源的情况）
 * @property iconResId 图标资源 ID（drawable 资源，优先使用）
 * @property labelTextId 标签文本资源 ID
 * @property selected 是否选中（用于高亮显示）
 */
data class MeDrawerDestination(
    val route: String,
    val icon: ImageVector? = null,
    @DrawableRes val iconResId: Int? = null,
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
                icon = Icons.Default.FavoriteBorder, // 朋友圈：暂时使用 Material Icon，后续可替换为 drawable
                labelTextId = R.string.me_drawer_moments
            ),
            MeDrawerDestination(
                route = MeRoutes.FAVORITES,
                icon = Icons.Default.Star, // 我的收藏：暂时使用 Material Icon，后续可替换为 drawable
                labelTextId = R.string.me_drawer_favorites
            ),
            MeDrawerDestination(
                route = MeRoutes.STATUS,
                iconResId = R.drawable.ic_new_mine_status, // 我的动态：使用 drawable 资源
                labelTextId = R.string.me_drawer_status
            ),
            MeDrawerDestination(
                route = MeRoutes.NOTIFICATIONS,
                iconResId = R.drawable.ic_new_mine_notice, // 消息通知：使用 drawable 资源
                labelTextId = R.string.me_drawer_notifications
            ),
            MeDrawerDestination(
                route = MeRoutes.PRIVACY,
                iconResId = R.drawable.ic_new_mine_privacy, // 隐私防护：使用 drawable 资源
                labelTextId = R.string.me_drawer_privacy
            ),
            MeDrawerDestination(
                route = MeRoutes.ACCOUNT_SECURITY,
                iconResId = R.drawable.ic_new_mine_security, // 账号安全：使用 drawable 资源
                labelTextId = R.string.me_drawer_account_security
            ),
            MeDrawerDestination(
                route = MeRoutes.SETTINGS,
                iconResId = R.drawable.ic_new_mine_setting, // 通用设置：使用 drawable 资源
                labelTextId = R.string.me_drawer_settings
            ),
        )
    }
}

