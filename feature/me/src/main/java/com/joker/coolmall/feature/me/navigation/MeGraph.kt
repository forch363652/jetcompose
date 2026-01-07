package com.joker.coolmall.feature.me.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * 我的（抽屉）模块导航图
 *
 * 该模块的页面通过 Drawer 从 MainShell push 进入（B1：返回回到 MainShell）
 */
fun NavGraphBuilder.meGraph(
    navController: NavHostController,
) {
    // navController 预留（后续若需要从页面内部跳转）
    momentsScreen()
    favoritesScreen()
    statusScreen()
    notificationsScreen()
    privacyScreen()
    accountSecurityScreen()
    settingsScreen()
    profileDetailScreen(
        onBackClick = { navController.popBackStack() }
    )
    santiaoIdDetailScreen(
        onBackClick = { navController.popBackStack() },
        onModifyClick = { santiaoId ->
            // TODO: 实现修改ID页面的导航
            // navController.navigate(...)
        }
    )
}


