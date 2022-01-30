plugins {
    val androidGradleVersion = "7.1.0"
    id("com.android.application") version androidGradleVersion apply false
    id("com.android.library") version androidGradleVersion apply false
    kotlin("android") version "1.6.10" apply false
}

subprojects {
    group = "net.daverix.gaugeview"
    version = "0.1"
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
