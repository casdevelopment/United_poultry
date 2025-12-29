package com.example.unitedpoultry.AdminShopModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminShopModule.Adapter.AdminShopListAdapter
import com.example.unitedpoultry.AdminShopModule.model.AdminShopModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminShopListBinding

class AdminShopListActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminShopListBinding
    private lateinit var adapter: AdminShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        binding.backArrow.setOnClickListener { finish() }

        binding.tvAreaName.text = intent.getStringExtra("AREA_NAME")

        // Sample data
        val data = mutableListOf(
            AdminShopModel("Ali Store", "Gulberg", "Active", 156, "5%", 12500),
            AdminShopModel("Khan Shop", "Model Town", "Active", 250, "14%", 20000),
            AdminShopModel("Bismillah Mart", "Johar Town", "Inactive", 78, "7%", 5200)
        )

        adapter = AdminShopListAdapter(data)

        binding.rvShopList.layoutManager = LinearLayoutManager(this)
        binding.rvShopList.adapter = adapter

        // Initial count
        binding.tvShopsCount.text = "${adapter.getFilteredCount()} Shops"

        // Search
        binding.etSearch.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
            binding.tvShopsCount.text = "${adapter.getFilteredCount()} Shops"
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AdminAddNewShopActivity::class.java)
            startActivity(intent)
        }
    }
}
