package com.example.unitedpoultry.History

import android.os.Bundle
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityCollectionHistoryBinding


class CollectionHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.mint
        )


        binding.backArrow.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra("name")
        val amount = intent.getIntExtra("amount", 0)

        // 🔹 Set data to TextViews
        binding.tvShopName.text = name
        binding.tvAmount.text = "Rs. $amount"

    }
}