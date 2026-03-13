package com.example.unitedpoultry.History

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : BaseActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        val imageUrl = intent.getStringExtra("image_url")

        Glide.with(this)
            .load(imageUrl)
            .placeholder(binding.ivPaymentSlip.drawable) // optional
            .into(binding.ivPaymentSlip)



//        photoView.setOnClickListener { finish() }

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}