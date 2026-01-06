plugins {
    id("com.joker.coolmall.android.feature")
}

android {
    namespace = "com.joker.coolmall.feature.groupchats"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 首版：仅提供群聊会话列表页占位 UI；后续复用 ConversationRepository 做过滤（type == GROUP）
}


