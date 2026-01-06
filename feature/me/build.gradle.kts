plugins {
    id("com.joker.coolmall.android.feature")
}

android {
    namespace = "com.joker.coolmall.feature.me"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 目前仅提供 Drawer 与占位页面；后续可按需补充依赖
}


