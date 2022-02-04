plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc03"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    val composeUiVersion = "1.1.0-rc01"
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.foundation:foundation:1.1.0-rc03")

    // following dependencies needed for preview
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    debugImplementation("androidx.savedstate:savedstate-ktx:1.1.0")
    debugImplementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    debugImplementation("androidx.core:core-ktx:1.7.0")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("lib") {
                from(components["release"])

                pom.withXml {
                    asNode().appendNode("description", "Compatibility library for using GaugeView in Jetpack Compose")
                }
            }
        }
    }
}