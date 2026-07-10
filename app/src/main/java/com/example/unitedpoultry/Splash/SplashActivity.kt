package com.example.unitedpoultry.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.Welcome.WelcomeActivity
import com.example.unitedpoultry.util.AppConstants
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class SplashActivity :  BaseActivity() {

    private val sessionManager: SessionManager by inject()

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var dotIndex = 0
    private val delay: Long = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        if(intent.getIntExtra("response",0)==401){
            sessionManager.logout()
        }

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        animateDots()

        handler.postDelayed({

            navigateFunction()

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

    private fun navigateFunction(){

        if (sessionManager.isLoggedIn()) {
            // setting token
            AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()

            //  getting user info
            AppConstants.userData =
                Gson().fromJson( sessionManager.getUserInfo(), LoginResponseModel::class.java)

            if(AppConstants.userData!!.role_id == 1){

                val intent = Intent(this@SplashActivity, AdminDashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()


            }else if (AppConstants.userData!!.role_id == 2){


                val intent = Intent(this@SplashActivity, RiderDashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        } else {
            // Check if the user is launching the app for the very first time
            if (sessionManager.isFirstTime()) {
                // First time ever? Show onboarding/welcome activity
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            } else {
                // Returning logged-out user? Skip onboarding, go straight to login
                startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
            }
            finish()
        }
    }


        override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
