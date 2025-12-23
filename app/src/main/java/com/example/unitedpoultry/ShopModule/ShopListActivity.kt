package com.example.unitedpoultry.ShopModule

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.Adapter.ShopsAdapter
import com.example.unitedpoultry.ShopModule.model.ShopsRecord
import com.example.unitedpoultry.databinding.ActivityShopListBinding
import androidx.core.widget.addTextChangedListener

class ShopListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopListBinding
    private lateinit var adapter: ShopsAdapter
    private var currentFilter = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        val areaName = intent.getStringExtra("AREA_NAME")

        binding.tvAreaName.setText(areaName)

        // Sample data
        val data = mutableListOf(
            ShopsRecord("Ali Store", "Gulberg", "VISITED", "10 Aug", 5, 0),
            ShopsRecord("Khan Shop", "Model Town", "PENDING", "05 Aug", 4, 2000),
            ShopsRecord("Bismillah Mart", "Johar Town", "OVERDUE", "25 Jul", 3, 5000)
        )

        adapter = ShopsAdapter(data)

        binding.rvShops.layoutManager = LinearLayoutManager(this)
        binding.rvShops.adapter = adapter

        updateFilterCounts()
        highlightFilter("ALL")

        // Search bar
        binding.etSearch.addTextChangedListener {
            adapter.filter(it.toString(), currentFilter)
        }

        // Filter clicks
        binding.filterAll.setOnClickListener { applyFilter("ALL") }
        binding.filterVisited.setOnClickListener { applyFilter("VISITED") }
        binding.filterPending.setOnClickListener { applyFilter("PENDING") }
        binding.filterOverdue.setOnClickListener { applyFilter("OVERDUE") }

    }

    private fun applyFilter(type: String) {
        currentFilter = type
        adapter.filter(binding.etSearch.text.toString(), type)
        highlightFilter(type)
    }

    private fun updateFilterCounts() {
        binding.filterAll.text = "All (${adapter.countByStatus("ALL")})"
        binding.filterVisited.text = "Visited (${adapter.countByStatus("VISITED")})"
        binding.filterPending.text = "Pending (${adapter.countByStatus("PENDING")})"
        binding.filterOverdue.text = "Overdue (${adapter.countByStatus("OVERDUE")})"
    }

    private fun highlightFilter(type: String) {
        val allFilters = listOf(binding.filterAll, binding.filterVisited, binding.filterPending, binding.filterOverdue)
        allFilters.forEach {
            it.setBackgroundResource(R.drawable.filter_bg)
            it.setTextColor(Color.BLACK)
        }

        val selected = when(type) {
            "VISITED" -> binding.filterVisited
            "PENDING" -> binding.filterPending
            "OVERDUE" -> binding.filterOverdue
            else -> binding.filterAll
        }

        selected.setBackgroundResource(R.drawable.filter_bg_selected)
        selected.setTextColor(Color.WHITE)
    }
}
