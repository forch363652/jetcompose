package com.joker.coolmall.navigation.routes

/**
 * 我的（抽屉）模块路由常量
 *
 * 注意：该模块是从主壳（Main）中通过 Drawer 入口进入的二级页面（B1：push 进入，返回回主壳）
 */
object MeRoutes {
    private const val ME_ROUTE = "me"

    /** 朋友圈 */
    const val MOMENTS = "$ME_ROUTE/moments"

    /** 我的收藏 */
    const val FAVORITES = "$ME_ROUTE/favorites"

    /** 我的动态 */
    const val STATUS = "$ME_ROUTE/status"

    /** 消息通知 */
    const val NOTIFICATIONS = "$ME_ROUTE/notifications"

    /** 隐私防护 */
    const val PRIVACY = "$ME_ROUTE/privacy"

    /** 账号安全 */
    const val ACCOUNT_SECURITY = "$ME_ROUTE/account_security"

    /** 通用设置 */
    const val SETTINGS = "$ME_ROUTE/settings"

    /** 个人资料详情 */
    const val PROFILE_DETAIL = "$ME_ROUTE/profile_detail"

    /** 三条ID详情 */
    const val SANTIAO_ID_DETAIL = "$ME_ROUTE/santiao_id_detail"
}


