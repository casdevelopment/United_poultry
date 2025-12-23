package com.example.unitedpoultry.History

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.databinding.ActivityCollectionHistoryBinding


class CollectionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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