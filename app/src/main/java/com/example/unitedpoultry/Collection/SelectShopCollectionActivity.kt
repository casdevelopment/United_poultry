package com.example.unitedpoultry.Collection


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.Adapter.SelectShopCollectionAdapter
import com.example.unitedpoultry.Collection.model.ShopCollectionModel
import com.example.unitedpoultry.databinding.ActivitySelectShopCollectionBinding


class SelectShopCollectionActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectShopCollectionBinding
    private lateinit var adapter: SelectShopCollectionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectShopCollectionBinding.inflate(layoutInflater)
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
            ShopCollectionModel("Jalal Sons", "Lahore, Punjab",10000),
            ShopCollectionModel("Imtiaz Store", "Karachi, Sindh",12500),
            ShopCollectionModel("Metro Cash & Carry", "Islamabad",1430000),
            ShopCollectionModel("Al-Fatah", "Rawalpindi",9298220),
            ShopCollectionModel("Green Mart", "Faisalabad",7807680)
        )

        adapter = SelectShopCollectionAdapter(data)

        binding.rvShops.layoutManager = LinearLayoutManager(this)
        binding.rvShops.adapter = adapter


        // Search bar
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            adapter.filter(query)
        }


    }
}
