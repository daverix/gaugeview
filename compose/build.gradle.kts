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
        kotlinCompilerExtensionVersion = "1.1.0-rc02"
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
    api(project(":view"))
    val composeUiVersion = "1.1.0-rc01"
    api("androidx.compose.ui:ui:$composeUiVersion")

    implementation("androidx.compose.foundation:foundation:1.1.0-rc03")
    implementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
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