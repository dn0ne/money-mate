plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
    id("io.realm.kotlin") version "1.13.0"
}

val mokoResourcesVersion = extra["moko.resources.version"] as String
val mokoMvvmVersion = extra["moko.mvvm.version"] as String
val kotlinxDateTimeVersion = extra["kotlinx.datetime.version"] as String
val kotlinxCoroutinesVersion = extra["kotlinx.coroutines.version"] as String
val realmVersion = extra["realm.version"] as String
val settingsVersion = extra["settings.version"] as String

kotlin {
    androidTarget()

    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java) {
            export("dev.icerock.moko:mvvm-core:$mokoMvvmVersion")
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // MOKO
                implementation("dev.icerock.moko:resources-compose:$mokoResourcesVersion")
                implementation("dev.icerock.moko:mvvm-compose:$mokoMvvmVersion")

                // Realm
                implementation("io.realm.kotlin:library-base:$realmVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

                // KotlinX DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")

                // Settings
                implementation("com.russhwolf:multiplatform-settings-no-arg:$settingsVersion")
                implementation("com.russhwolf:multiplatform-settings-coroutines:$settingsVersion")

                // Mobile Ads SDK
                implementation("com.google.android.gms:play-services-ads:22.6.0")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.activity:activity-compose:1.8.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.dn0ne.moneymate"
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.dn0ne.moneymate"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    sourceSets["main"].resources.exclude("src/commonMain/resources/MR")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    commonMainApi("dev.icerock.moko:mvvm-core:$mokoMvvmVersion")
    commonMainApi("dev.icerock.moko:mvvm-compose:$mokoMvvmVersion")
    commonMainApi("dev.icerock.moko:mvvm-flow:$mokoMvvmVersion")
    commonMainApi("dev.icerock.moko:mvvm-flow-compose:$mokoMvvmVersion")
}
