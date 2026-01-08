package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.datastore.datasource.session.SessionDataSource
import com.joker.coolmall.core.model.entity.Auth
import com.joker.coolmall.core.model.entity.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 会话仓库（单一数据源）
 * 对外暴露 StateFlow<Session>，所有模块共享同一会话状态
 */
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDataSource: SessionDataSource
) {
    /**
     * 会话状态流（单一数据源）
     * 所有需要登录态的地方都订阅这个流
     */
    val session: Flow<Session> = sessionDataSource.getSession()

    /**
     * 是否已登录（从会话状态映射）
     */
    val isLoggedIn: Flow<Boolean> = session.map { it.isLoggedIn && !it.isTokenExpired() }

    /**
     * 用户ID流
     */
    val userId: Flow<Long> = session.map { it.userId }

    /**
     * Token流
     */
    val token: Flow<String> = session.map { it.token }

    /**
     * 登录成功后保存会话信息
     */
    suspend fun saveSessionFromAuth(auth: Auth, userId: Long) {
        val session = Session(
            userId = userId,
            token = auth.token,
            refreshToken = auth.refreshToken,
            isLoggedIn = true,
            expire = auth.expire,
            refreshExpire = auth.refreshExpire,
            createdAt = auth.createdAt
        )
        sessionDataSource.saveSession(session)
    }

    /**
     * 更新token（刷新token时使用）
     */
    suspend fun updateToken(
        token: String,
        refreshToken: String,
        expire: Long,
        refreshExpire: Long
    ) {
        sessionDataSource.updateToken(token, refreshToken, expire, refreshExpire)
    }

    /**
     * 登出（清除会话）
     */
    suspend fun logout() {
        sessionDataSource.clearSession()
    }

    /**
     * 获取当前会话（同步方法，用于需要立即获取的场景）
     */
    fun getCurrentSession(): Session {
        return sessionDataSource.getCurrentSession()
    }
}

