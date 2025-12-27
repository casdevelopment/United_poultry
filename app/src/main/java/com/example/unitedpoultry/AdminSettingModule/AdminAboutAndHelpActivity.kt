package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.databinding.ActivityAdminAboutAndHelpBinding


class AdminAboutAndHelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAboutAndHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminAboutAndHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val name = intent.getStringExtra("name") ?: ""
//        val address = intent.getStringExtra("address") ?: ""
//        val discount = intent.getStringExtra("Discount") ?: ""


//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvStatus.text = status



//        binding.btnSave.setOnClickListener {
//
//            val intent = Intent(this, SettingHomeActivity::class.java)
//            startActivity(intent)
//        }


        binding.backArrow.setOnClickListener {
            finish()
        }

    }
}
