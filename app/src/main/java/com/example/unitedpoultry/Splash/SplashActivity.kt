package com.example.unitedpoultry.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Welcome.WelcomeActivity

class SplashActivity :  BaseActivity() {

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var dotIndex = 0
    private val delay: Long = 400 // milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        animateDots()

        handler.postDelayed({
            val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Prevent returning to splash when back is pressed
        }, 4000) // 4000 milliseconds = 4 seconds
    }

    private fun animateDots() {
        val dots = arrayOf(dot1, dot2, dot3)
        val defaultColor = android.graphics.Color.WHITE
        val highlightColor = android.graphics.Color.parseColor("#FEAE55")

        handler.post(object : Runnable {
            override fun run() {
                // Reset all dots to white
                for (dot in dots) {
                    dot.setTextColor(defaultColor)
                }
                // Highlight current dot
                dots[dotIndex].setTextColor(highlightColor)

                dotIndex = (dotIndex + 1) % dots.size
                handler.postDelayed(this, delay)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
