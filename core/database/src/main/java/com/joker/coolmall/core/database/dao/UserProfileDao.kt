package com.joker.coolmall.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joker.coolmall.core.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户资料数据访问对象
 */
@Dao
interface UserProfileDao {
    /**
     * 插入或更新用户资料
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserProfileEntity)

    /**
     * 获取用户资料（Flow）
     */
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun getUserProfile(userId: Long): Flow<UserProfileEntity?>

    /**
     * 获取用户资料（同步）
     */
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getUserProfileSync(userId: Long): UserProfileEntity?

    /**
     * 删除用户资料
     */
    @Query("DELETE FROM user_profile WHERE id = :userId")
    suspend fun deleteUserProfile(userId: Long)

    /**
     * 清空所有用户资料
     */
    @Query("DELETE FROM user_profile")
    suspend fun clearAll()

    /**
     * 获取第一个用户资料（用于 fallback，当 Session 未初始化时）
     */
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getFirstUserProfile(): UserProfileEntity?
}

