package com.example.unitedpoultry.NewSale

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.NewSale.model.ShopModel
import com.example.unitedpoultry.databinding.ActivitySelectShopBinding

class SelectShopActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectShopBinding
    private lateinit var adapter: SelectShopAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        binding.backArrow.setOnClickListener {
            finish()
        }

        //val areaName = intent.getStringExtra("AREA_NAME")

       // binding.tvAreaName.setText(areaName)

        // Sample data
        val data = mutableListOf(
            ShopModel("Jalal Sons", "Lahore, Punjab"),
            ShopModel("Imtiaz Store", "Karachi, Sindh"),
            ShopModel("Metro Cash & Carry", "Islamabad"),
            ShopModel("Al-Fatah", "Rawalpindi"),
            ShopModel("Green Mart", "Faisalabad")
        )

        adapter = SelectShopAdapter(data)

        binding.rvShops.layoutManager = LinearLayoutManager(this)
        binding.rvShops.adapter = adapter


        // Search bar
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            adapter.filter(query)
        }


    }
}
