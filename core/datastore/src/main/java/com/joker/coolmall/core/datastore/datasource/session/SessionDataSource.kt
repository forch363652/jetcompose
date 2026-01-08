package com.joker.coolmall.core.datastore.datasource.session

import com.joker.coolmall.core.model.entity.Session
import kotlinx.coroutines.flow.Flow

/**
 * 会话数据源接口
 * 负责管理登录态、用户ID、token等会话信息
 */
interface SessionDataSource {
    /**
     * 获取会话状态流
     */
    fun getSession(): Flow<Session>

    /**
     * 保存会话信息
     */
    suspend fun saveSession(session: Session)

    /**
     * 清除会话信息（登出）
     */
    suspend fun clearSession()

    /**
     * 更新用户ID
     */
    suspend fun updateUserId(userId: Long)

    /**
     * 更新token
     */
    suspend fun updateToken(token: String, refreshToken: String, expire: Long, refreshExpire: Long)

    /**
     * 获取当前会话（同步方法）
     */
    fun getCurrentSession(): Session
}

