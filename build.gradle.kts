buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.7.6")
    }
}

plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.android.application") version "8.1.1" apply false
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url =  uri("https://jitpack.io") }
    }
}
