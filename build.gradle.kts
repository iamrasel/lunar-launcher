// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.7.10"
    extra ["kotlinVersion"] = kotlinVersion

    dependencies {
        classpath (kotlin("gradle-plugin", version = kotlinVersion))
    }
}

plugins {
    id ("com.android.application") version "7.3.1" apply false
    id ("com.android.library") version "7.3.1" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
