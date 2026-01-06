plugins {
    id("com.joker.coolmall.android.feature")
}

android {
    namespace = "com.joker.coolmall.feature.groups"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 首版：仅提供分组页占位 UI；后续接入联系人分组管理（Room + Flow）
}


