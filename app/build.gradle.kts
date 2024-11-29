plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.lirixgroup.tspdevotionaldraft"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lirixgroup.tspdevotionaldraft"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Corrected: Closing the `tasks.withType<JavaCompile>` block
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.work:work-runtime:2.10.0")

    // Removed the AdMob dependency as requested
    implementation("com.google.android.gms:play-services-ads:23.5.0")

    implementation("com.google.guava:guava:30.1-android")


}
