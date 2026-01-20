package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.unitedpoultry.AdminShopModule.AdminEditShopActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.Splash.SplashActivity
import com.example.unitedpoultry.Welcome.WelcomeActivity
import com.example.unitedpoultry.databinding.ActivitySettingHomeBinding
import com.example.unitedpoultry.util.AppConstants.userData
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject


class SettingHomeActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingHomeBinding

    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivitySettingHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )

        showData()
        onclick()





//        binding.printerSettings.setOnClickListener {
//
//            val intent = Intent(this, AdminPrinterSettingActivity::class.java)
//            startActivity(intent)
//        }

//        binding.appAndAbout.setOnClickListener {
//
//            val intent = Intent(this, AdminAboutAndHelpActivity::class.java)
//            startActivity(intent)
//        }





//        binding.backArrow.setOnClickListener {
//            finish()
//        }

    }

    private fun showData(){

        binding.tvName.text = userData?.name ?: "User Name"
        binding.tvEmail.text = userData?.email ?: "Email"

        binding.tvInitials.text = getInitials(userData?.name)

    }

    private fun  onclick(){

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

                sessionManager.logout()
                startActivity(Intent(this, SplashActivity::class.java))
                finish()

            }

            dialog.show()
        }




        binding.editProfile.setOnClickListener {

            val intent = Intent(this, AdminEditProfileActivity::class.java)
            intent.putExtra("NAME",  userData?.name)
            intent.putExtra("EMAIL", userData?.email)
            intent.putExtra("PHONE_NUMBER", userData?.phone_number)
            intent.putExtra("USERNAME", userData?.username)
            intent.putExtra("ADDRESS", userData?.address)
            intent.putExtra("INITIALS", getInitials(userData?.name))
            startActivity(intent)

        }

        binding.rateManagement.setOnClickListener {

            val intent = Intent(this, AdminRateManagmentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return "U" // Default initial
        val words = name.trim().split(" ")
        return when {
            words.size >= 2 -> "${words[0][0]}${words[1][0]}".uppercase()
            words.isNotEmpty() -> "${words[0][0]}".uppercase()
            else -> "U"
        }
    }

}
