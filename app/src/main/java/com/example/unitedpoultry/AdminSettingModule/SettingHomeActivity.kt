package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Welcome.WelcomeActivity
import com.example.unitedpoultry.databinding.ActivitySettingHomeBinding
import com.google.android.material.button.MaterialButton


class SettingHomeActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivitySettingHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )



        binding.logoutCard.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
            val btnLogout = dialogView.findViewById<MaterialButton>(R.id.btnLogout)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnLogout.setOnClickListener {

                dialog.dismiss()

                // Navigate to WelcomeActivity & clear back stack
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // Finish current activity
                finish()
            }

            dialog.show()
        }




        binding.editProfile.setOnClickListener {

            val intent = Intent(this, AdminEditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.rateManagement.setOnClickListener {

            val intent = Intent(this, AdminRateManagmentActivity::class.java)
            startActivity(intent)
        }

        binding.printerSettings.setOnClickListener {

            val intent = Intent(this, AdminPrinterSettingActivity::class.java)
            startActivity(intent)
        }

        binding.appAndAbout.setOnClickListener {

            val intent = Intent(this, AdminAboutAndHelpActivity::class.java)
            startActivity(intent)
        }





//        binding.backArrow.setOnClickListener {
//            finish()
//        }

    }
}
