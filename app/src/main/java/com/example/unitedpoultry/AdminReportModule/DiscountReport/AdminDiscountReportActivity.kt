package com.example.unitedpoultry.AdminReportModule.DiscountReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAreaReportBinding
import com.example.unitedpoultry.databinding.ActivityAdminDiscountReportBinding


class AdminDiscountReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminDiscountReportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDiscountReportBinding.inflate(layoutInflater)
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

