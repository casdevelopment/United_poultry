package com.example.unitedpoultry.AdminArea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAddNewAreaBinding
import com.example.unitedpoultry.databinding.ActivityEditAreaBinding

class AddNewAreaActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewAreaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAddNewAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        binding.backArrow.setOnClickListener {
            finish()
        }

        // Optional: handle save button click
//        binding.btnSave.setOnClickListener {
//            // TODO: Save updated data
//        }
    }
}
