package com.joker.coolmall.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joker.coolmall.core.model.entity.User

/**
 * 用户资料数据库实体
 * 
 * 存储用户的基本信息，用于 offline-first 架构
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long,

    val unionid: String = "",
    val avatarUrl: String? = null,
    val nickName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val location: String? = null,
    val gender: Int = 0,
    val qrCodeUrl: String? = null,
    val status: Int = 1,
    val loginType: String = "0",
    val isRealNameAuthenticationRequired: Boolean = false,
    val isThereADeviceLock: Boolean = false,
    val createTime: String? = null,
    val updateTime: String? = null,

    /**
     * 数据更新时间（用于判断是否需要刷新）
     */
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * 转换为 User 模型
     * 从数据库实体创建 User 对象
     */
    fun toUser(): User {
        return User(
            id = id,
            unionid = unionid,
            avatarUrl = avatarUrl,
            nickName = nickName,
            phone = phone,
            email = email,
            location = location,
            qrCodeUrl = qrCodeUrl,
            gender = gender,
            status = status,
            loginType = loginType,
            isRealNameAuthenticationRequired = isRealNameAuthenticationRequired,
            isThereADeviceLock = isThereADeviceLock,
            createTime = createTime,
            updateTime = updateTime
        )
    }

    companion object {
        /**
         * 从 User 模型创建实体
         */
        fun fromUser(user: User): UserProfileEntity {
            return UserProfileEntity(
                id = user.id,
                unionid = user.unionid,
                avatarUrl = user.avatarUrl,
                nickName = user.nickName,
                phone = user.phone,
                email = user.email,
                location = user.location,
                gender = user.gender,
                qrCodeUrl = user.qrCodeUrl,
                status = user.status,
                loginType = user.loginType,
                isRealNameAuthenticationRequired = user.isRealNameAuthenticationRequired,
                isThereADeviceLock = user.isThereADeviceLock,
                createTime = user.createTime,
                updateTime = user.updateTime,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}

