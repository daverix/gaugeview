plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 19
        targetSdk = 31
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
    api(kotlin("stdlib-jdk8"))
    api("androidx.core:core-ktx:1.7.0")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("lib") {
                from(components["release"])

                pom.withXml {
                    asNode().appendNode("description", "A library that provides a gauge view for use in android projects")
                }
            }
        }
    }
}
