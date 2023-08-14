// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.9.0"
    extra ["kotlinVersion"] = kotlinVersion

    dependencies {
        classpath (kotlin("gradle-plugin", version = kotlinVersion))
    }
}

plugins {
    id ("com.android.application") version "8.1.0" apply false
    id ("com.android.library") version "8.1.0" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
