package com.example.unitedpoultry.AdminReportModule.ReceivablesReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminReceivableReportBinding


class AdminReceivableReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminReceivableReportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReceivableReportBinding.inflate(layoutInflater)
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

