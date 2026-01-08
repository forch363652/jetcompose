package com.joker.coolmall.feature.me.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joker.coolmall.feature.me.view.AccountSecurityScreen
import com.joker.coolmall.feature.me.view.FavoritesScreen
import com.joker.coolmall.feature.me.view.MomentsScreen
import com.joker.coolmall.feature.me.view.ModifySantiaoIdRoute
import com.joker.coolmall.feature.me.view.NotificationsScreen
import com.joker.coolmall.feature.me.view.PrivacyScreen
import com.joker.coolmall.feature.me.view.ProfileDetailRoute
import com.joker.coolmall.feature.me.view.QRCodeRoute
import com.joker.coolmall.feature.me.view.SantiaoIdDetailRoute
import com.joker.coolmall.feature.me.view.SettingsScreen
import com.joker.coolmall.feature.me.view.StatusScreen
import com.joker.coolmall.navigation.routes.MeRoutes

fun NavGraphBuilder.momentsScreen() {
    composable(route = MeRoutes.MOMENTS) { MomentsScreen() }
}

fun NavGraphBuilder.favoritesScreen() {
    composable(route = MeRoutes.FAVORITES) { FavoritesScreen() }
}

fun NavGraphBuilder.statusScreen() {
    composable(route = MeRoutes.STATUS) { StatusScreen() }
}

fun NavGraphBuilder.notificationsScreen() {
    composable(route = MeRoutes.NOTIFICATIONS) { NotificationsScreen() }
}

fun NavGraphBuilder.privacyScreen() {
    composable(route = MeRoutes.PRIVACY) { PrivacyScreen() }
}

fun NavGraphBuilder.accountSecurityScreen() {
    composable(route = MeRoutes.ACCOUNT_SECURITY) { AccountSecurityScreen() }
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = MeRoutes.SETTINGS) { SettingsScreen() }
}

fun NavGraphBuilder.profileDetailScreen(
    onBackClick: () -> Unit,
) {
    composable(route = MeRoutes.PROFILE_DETAIL) {
        ProfileDetailRoute(onBackClick = onBackClick)
    }
}

fun NavGraphBuilder.santiaoIdDetailScreen(
    onBackClick: () -> Unit,
    onModifyClick: (String) -> Unit = {},
) {
    composable(route = MeRoutes.SANTIAO_ID_DETAIL) {
        SantiaoIdDetailRoute(
            onBackClick = onBackClick,
            onModifyClick = onModifyClick,
        )
    }
}

/**
 * 修改三条ID页面导航
 */
fun NavGraphBuilder.modifySantiaoIdScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = {},
) {
    composable(
        route = "${MeRoutes.MODIFY_SANTIAO_ID}?currentId={currentId}",
        arguments = listOf(
            navArgument("currentId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { backStackEntry ->
        val currentId = backStackEntry.arguments?.getString("currentId") ?: ""
        ModifySantiaoIdRoute(
            currentId = currentId,
            onBackClick = onBackClick,
            onSaveSuccess = onSaveSuccess,
        )
    }
}

/**
 * 二维码页面导航
 */
fun NavGraphBuilder.qrCodeScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = MeRoutes.QR_CODE,
        arguments = listOf(
            navArgument(MeRoutes.QR_CODE_URL_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(MeRoutes.QR_CODE_USER_NAME_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(MeRoutes.QR_CODE_AVATAR_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        QRCodeRoute(onBackClick = onBackClick)
    }
}


