plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("androidx.core:core-splashscreen:1.0.1")
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

                // Mobile Ads SDK
                implementation("com.google.android.gms:play-services-ads:22.6.0")

                // Koin
                implementation(platform("io.insert-koin:koin-bom:3.5.3"))
                implementation("io.insert-koin:koin-android")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.dn0ne.moneymate"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.dn0ne.MoneyMate"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    kotlin {
        jvmToolchain(17)
    }
}
