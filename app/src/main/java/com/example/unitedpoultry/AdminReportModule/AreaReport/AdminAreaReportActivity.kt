package com.example.unitedpoultry.AdminReportModule.AreaReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAreaReportBinding


class AdminAreaReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminAreaReportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAreaReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )

        binding.backArrow.setOnClickListener {
            finish()
        }

    }

}

