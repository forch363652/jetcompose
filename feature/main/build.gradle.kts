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
    implementation(project(":feature:me"))
    implementation(project(":feature:contacts"))
    implementation(project(":feature:groupchats"))
    implementation(project(":feature:groups"))

    // lottie 动画
    // https://airbnb.io/lottie/#/android-compose
    implementation(libs.lottie.compose)
}