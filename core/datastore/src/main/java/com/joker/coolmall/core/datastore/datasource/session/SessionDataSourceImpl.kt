package com.joker.coolmall.core.datastore.datasource.session

import com.joker.coolmall.core.model.entity.Session
import com.joker.coolmall.core.util.storage.MMKVUtils
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * 会话数据源实现
 * 使用 MMKV 持久化，StateFlow 提供响应式流
 */
class SessionDataSourceImpl @Inject constructor() : SessionDataSource {

    companion object {
        private const val KEY_SESSION = "session_info"
    }

    private val json = Json { ignoreUnknownKeys = true }

    // 使用 StateFlow 提供响应式会话状态
    private val _sessionFlow = MutableStateFlow<Session>(loadSessionFromStorage())

    override fun getSession(): Flow<Session> = _sessionFlow.asStateFlow()

    override suspend fun saveSession(session: Session) {
        val sessionJson = json.encodeToString(Session.serializer(), session)
        MMKVUtils.putString(KEY_SESSION, sessionJson)
        _sessionFlow.value = session
    }

    override suspend fun clearSession() {
        MMKVUtils.remove(KEY_SESSION)
        _sessionFlow.value = Session.Empty
    }

    override suspend fun updateUserId(userId: Long) {
        val currentSession = _sessionFlow.value
        val updatedSession = currentSession.copy(userId = userId)
        saveSession(updatedSession)
    }

    override suspend fun updateToken(
        token: String,
        refreshToken: String,
        expire: Long,
        refreshExpire: Long
    ) {
        val currentSession = _sessionFlow.value
        val updatedSession = currentSession.copy(
            token = token,
            refreshToken = refreshToken,
            expire = expire,
            refreshExpire = refreshExpire,
            createdAt = System.currentTimeMillis()
        )
        saveSession(updatedSession)
    }

    /**
     * 获取当前会话（同步方法）
     */
    override fun getCurrentSession(): Session = _sessionFlow.value

    /**
     * 从存储中加载会话信息
     */
    private fun loadSessionFromStorage(): Session {
        val sessionJson = MMKVUtils.getString(KEY_SESSION, "")
        if (sessionJson.isEmpty()) return Session.Empty

        return try {
            json.decodeFromString(Session.serializer(), sessionJson)
        } catch (e: Exception) {
            Session.Empty
        }
    }
}

