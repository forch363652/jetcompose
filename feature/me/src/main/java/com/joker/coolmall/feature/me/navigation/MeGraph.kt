package com.joker.coolmall.feature.me.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.joker.coolmall.feature.me.navigation.qrCodeScreen
import com.joker.coolmall.navigation.routes.MeRoutes

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
            navController.navigate("${MeRoutes.MODIFY_SANTIAO_ID}?currentId=$santiaoId") {
                launchSingleTop = true
            }
        }
    )
    
    modifySantiaoIdScreen(
        onBackClick = { navController.popBackStack() },
        onSaveSuccess = {
            // 保存成功后，可以刷新数据或显示提示
            // TODO: 可以在这里添加刷新逻辑
        }
    )
    
    qrCodeScreen(
        onBackClick = { navController.popBackStack() }
    )
}


