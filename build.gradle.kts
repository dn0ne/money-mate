plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("dev.icerock.mobile.multiplatform-resources").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}