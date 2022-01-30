plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "net.daverix.gaugeview.sample"
        minSdk = 19
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":view"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("androidx.appcompat:appcompat:1.4.1")
}
