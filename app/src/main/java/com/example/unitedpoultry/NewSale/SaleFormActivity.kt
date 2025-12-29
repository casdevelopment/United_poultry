package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.NewSale.model.ShopModel
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.databinding.ActivitySelectShopBinding

class SaleFormActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleFormBinding
    private lateinit var adapter: SelectShopAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        binding.backArrow.setOnClickListener {
            finish()
        }
        val name = intent.getStringExtra("name") ?: "N/A"

        val address = intent.getStringExtra("address")?: "N/A"

        // 🔹 Set data to TextViews
        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)

        binding.btnConfirmSale.setOnClickListener {
            val intent = Intent(this, SaleConfirmationActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials",  getInitials(name))
            startActivity(intent)

        }




    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
