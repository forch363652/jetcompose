package com.joker.coolmall.core.data.repository

import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.model.preview.previewUser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 假数据仓库（用于预览和测试）
 * 
 * 仅在开发/预览模式下使用，避免发布版携带预览数据
 * 通过 BuildConfig.DEBUG 控制是否启用
 */
@Singleton
class FakeUserInfoRepository @Inject constructor() {
    /**
     * 获取预览用户数据
     * 
     * @return 预览用户数据
     */
    fun getPreviewUser(): User {
        return previewUser.copy(
            // 可以在这里添加额外的预览数据字段
            // 例如：email = "preview@example.com"
        )
    }
}

