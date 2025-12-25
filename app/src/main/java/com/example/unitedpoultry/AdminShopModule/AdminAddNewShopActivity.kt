package com.example.unitedpoultry.AdminShopModule

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAddNewShopBinding
import com.example.unitedpoultry.databinding.ActivityAdminShopDetailsBinding


class AdminAddNewShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddNewShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminAddNewShopBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val intent = Intent(this, AdminDashBoardActivity::class.java)
            startActivity(intent)
        }

    }
}
