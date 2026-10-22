plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.nfsu2"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.nfsu2"
        minSdk = 29
        targetSdk = 31          // Android 12 on the RG Rotate
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "DOLPHIN_PKG", "\"org.dolphinemu.dolphinemu\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
