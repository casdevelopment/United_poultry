package com.example.unitedpoultry.History

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.R
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.databinding.ActivitySaleHistoryBinding

class SaleHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleHistoryBinding.inflate(layoutInflater)
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