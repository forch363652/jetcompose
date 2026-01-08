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
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    
    // Coil for image loading
    implementation(libs.coil.compose)
    
    // ZXing for QR code generation
    implementation("com.google.zxing:core:3.5.1")
}


