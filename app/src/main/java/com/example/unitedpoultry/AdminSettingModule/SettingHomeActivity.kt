package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.databinding.ActivitySettingHomeBinding


class SettingHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivitySettingHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val name = intent.getStringExtra("name") ?: ""
//        val address = intent.getStringExtra("address") ?: ""
//        val discount = intent.getStringExtra("Discount") ?: ""


//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvStatus.text = status



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
