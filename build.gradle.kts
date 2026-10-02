buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.4.1")
    }
}
plugins {
    id("com.android.application") version "9.4.1" apply false
    alias(libs.plugins.kotlin.compose) apply false
}
