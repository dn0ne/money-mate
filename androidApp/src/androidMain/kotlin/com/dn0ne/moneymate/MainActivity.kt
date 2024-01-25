package com.dn0ne.moneymate

import App
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        val darkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        setContent {
            App(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            )
        }
    }
}