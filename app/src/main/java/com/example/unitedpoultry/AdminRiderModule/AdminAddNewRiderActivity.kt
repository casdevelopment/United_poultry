package com.example.unitedpoultry.AdminRiderModule

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAddNewRiderBinding


class AdminAddNewRiderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddNewRiderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminAddNewRiderBinding.inflate(layoutInflater)
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
