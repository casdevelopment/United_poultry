package com.example.unitedpoultry

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity : AppCompatActivity() {

    // Call this in onCreate() of derived activity
    fun configureStatusBar(isLightBackground: Boolean, colorResId: Int) {
        val window = this.window
        window.statusBarColor = ContextCompat.getColor(this, colorResId)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set icon color based on background
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = isLightBackground
    }
}
