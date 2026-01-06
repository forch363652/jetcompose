plugins {
    id("com.joker.coolmall.android.feature")
}

android {
    namespace = "com.joker.coolmall.feature.contacts"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 首版：仅提供联系人页占位 UI；后续接入 Room/Flow 后再补 core:data 等依赖
}


