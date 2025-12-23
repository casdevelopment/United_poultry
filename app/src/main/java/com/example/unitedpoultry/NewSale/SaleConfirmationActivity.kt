package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.NewSale.model.ShopModel
import com.example.unitedpoultry.databinding.ActivitySaleConfirmationBinding
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.databinding.ActivitySelectShopBinding

class SaleConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleConfirmationBinding
    private lateinit var adapter: SelectShopAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }
        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val initials = intent.getStringExtra("initials") ?: "Unknown"

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = initials

        binding.btnSubmitSale.setOnClickListener {
            val intent = Intent(this, SaleSuccessActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials", initials)
            startActivity(intent)

        }


    }
}
