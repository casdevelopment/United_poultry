package com.example.unitedpoultry.AdminArea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.databinding.ActivityEditAreaBinding

class EditAreaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAreaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityEditAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the data from intent
        val areaName = intent.getStringExtra("AREA_NAME") ?: ""
        val areaAddress = intent.getStringExtra("AREA_ADDRESS") ?: ""

        // Set data to EditTexts using binding
        binding.etAreaName.setText(areaName)
        binding.etAreaAddress.setText(areaAddress)

        binding.backArrow.setOnClickListener {
            finish()
        }

        // Optional: handle save button click
//        binding.btnSave.setOnClickListener {
//            // TODO: Save updated data
//        }
    }
}
