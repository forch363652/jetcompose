plugins {
    id("com.joker.coolmall.android.feature")
}

android {
    namespace = "com.joker.coolmall.feature.main"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:network"))

    // lottie 动画
    // https://airbnb.io/lottie/#/android-compose
    implementation(libs.lottie.compose)
}