package com.example.unitedpoultry.AdminReportModule.SaleReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminSalesReportBinding


class AdminSalesReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminSalesReportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSalesReportBinding.inflate(layoutInflater)
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

