package com.joker.coolmall.feature.me.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.joker.coolmall.feature.me.view.AccountSecurityScreen
import com.joker.coolmall.feature.me.view.FavoritesScreen
import com.joker.coolmall.feature.me.view.MomentsScreen
import com.joker.coolmall.feature.me.view.NotificationsScreen
import com.joker.coolmall.feature.me.view.PrivacyScreen
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


