package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

/**
 * 会话状态模型（单一数据源）
 * 包含登录态、用户ID、token等信息
 */
@Serializable
data class Session(
    /**
     * 用户ID
     */
    val userId: Long = 0,

    /**
     * 访问令牌
     */
    val token: String = "",

    /**
     * 刷新令牌
     */
    val refreshToken: String = "",

    /**
     * 是否已登录
     */
    val isLoggedIn: Boolean = false,

    /**
     * 令牌过期时间（秒）
     */
    val expire: Long = 0,

    /**
     * 刷新令牌过期时间（秒）
     */
    val refreshExpire: Long = 0,

    /**
     * 令牌创建时间（毫秒）
     */
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * 检查访问令牌是否过期
     */
    fun isTokenExpired(): Boolean {
        if (!isLoggedIn || token.isEmpty()) return true
        val currentTime = System.currentTimeMillis()
        val expirationTime = createdAt + (expire * 1000)
        return currentTime >= expirationTime
    }

    /**
     * 检查刷新令牌是否过期
     */
    fun isRefreshTokenExpired(): Boolean {
        if (!isLoggedIn || refreshToken.isEmpty()) return true
        val currentTime = System.currentTimeMillis()
        val expirationTime = createdAt + (refreshExpire * 1000)
        return currentTime >= expirationTime
    }

    /**
     * 检查令牌是否需要刷新（过期前15分钟）
     */
    fun shouldRefresh(): Boolean {
        if (!isLoggedIn || token.isEmpty()) return false
        val currentTime = System.currentTimeMillis()
        val refreshTime = createdAt + (expire * 1000) - (15 * 60 * 1000) // 提前15分钟刷新
        return currentTime >= refreshTime && !isRefreshTokenExpired()
    }

    companion object {
        /**
         * 空会话（未登录状态）
         */
        val Empty = Session()
    }
}

