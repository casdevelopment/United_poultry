package com.example.unitedpoultry.Profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Profile.Adapter.AreaStatusAdapter
import com.example.unitedpoultry.Profile.model.AreaStatusModel
import com.example.unitedpoultry.databinding.ActivityMyPerformanceBinding


class MyPerformanceActivity : BaseActivity() {

    private lateinit var binding: ActivityMyPerformanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.circularStatus.max = 100
        binding.circularStatus.setProgress(70, true) // true = animate


        binding.rvAreaStatus.layoutManager = LinearLayoutManager(this)

        val areaList = listOf(
            AreaStatusModel("DHA Phase 5", 75),
            AreaStatusModel("Gulberg", 85),
            AreaStatusModel("Model Town", 60),
            AreaStatusModel("Johar Town", 40),
            AreaStatusModel("Bahria Town", 90)
        )

        binding.rvAreaStatus.adapter = AreaStatusAdapter(this, areaList)

    }
}
