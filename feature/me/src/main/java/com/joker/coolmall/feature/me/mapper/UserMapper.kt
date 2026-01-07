package com.joker.coolmall.feature.me.mapper

import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.feature.me.state.ProfileDetailUiState

/**
 * User 到 ProfileDetailUiState 的映射函数
 * 
 * 职责：
 * - 将领域模型（User）转换为 UI 状态（ProfileDetailUiState）
 * - 便于复用和单测
 * - 减少 ViewModel 内字段复制
 */
object UserMapper {
    /**
     * 将 User 转换为 ProfileDetailUiState
     * 
     * @param user 用户信息
     * @param isPhoneVisible 手机号是否可见（默认 false）
     * @return ProfileDetailUiState
     */
    fun User.toUiState(
        isPhoneVisible: Boolean = false,
    ): ProfileDetailUiState {
        return ProfileDetailUiState(
            isLoading = false,
            avatarUrl = this.avatarUrl,
            userName = this.nickName,
            santiaoId = this.id.toString(),
            phone = this.phone,
            isPhoneVisible = isPhoneVisible,
            imid = this.unionid,
            gender = this.gender,
            // 预览数据：模拟邮箱和所在地
            // 实际项目中这些字段应该从 User 模型或单独的数据源获取
            email = "1014945530@qq.com",
            location = "广东省 深圳市",
            errorMessage = null,
        )
    }
}

