// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
   // alias(libs.plugins.kotlin.android) apply false // Include Kotlin plugin if using Kotlin
}

allprojects {
    repositories {
//        google()
    //    mavenCentral()
    }
}


val javaHome = file("C:/Program Files/Eclipse Adoptium/jdk-21.0.5.11-hotspot")