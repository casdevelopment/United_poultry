package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditProfileBinding
import com.example.unitedpoultry.databinding.ActivityAdminRateManagmentBinding


class AdminRateManagmentActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRateManagmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminRateManagmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


//        val name = intent.getStringExtra("name") ?: ""
//        val address = intent.getStringExtra("address") ?: ""
//        val discount = intent.getStringExtra("Discount") ?: ""


//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvStatus.text = status



        binding.btnSave.setOnClickListener {

            val intent = Intent(this, SettingHomeActivity::class.java)
            startActivity(intent)
        }


        binding.backArrow.setOnClickListener {
            finish()
        }

    }
}
