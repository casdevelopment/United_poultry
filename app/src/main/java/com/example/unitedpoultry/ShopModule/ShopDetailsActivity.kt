package com.example.unitedpoultry.ShopModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.Collection.SelectShopCollectionActivity
import com.example.unitedpoultry.NewSale.SaleSuccessActivity
import com.example.unitedpoultry.NewSale.SelectShopActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.Adapter.RecentActivityAdapter
import com.example.unitedpoultry.ShopModule.model.RecentActivityModel
import com.example.unitedpoultry.databinding.ActivityShopDetailsBinding

class ShopDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopDetailsBinding
    private lateinit var adapter: RecentActivityAdapter
    private val activityList = mutableListOf<RecentActivityModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize DataBinding
        binding = ActivityShopDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnNewSale.setOnClickListener {
            val intent = Intent(this, SelectShopActivity::class.java)
            startActivity(intent)
        }

        binding.btnCollectPayment.setOnClickListener {
            val intent = Intent(this, SelectShopCollectionActivity::class.java)
            startActivity(intent)
        }


        // ✅ Receive Intent Data
        val areaName = intent.getStringExtra("AREA_NAME") ?: ""
        val address = intent.getStringExtra("ADDRESS") ?: ""

        // ✅ Set UI values using binding
        binding.tvShopName.text = areaName
        binding.tvAddress.text = address
        binding.tvInitials.text = getInitials(areaName)

        // ✅ RecyclerView setup
        binding.recyclerRecentActivities.layoutManager =
            LinearLayoutManager(this)

        // ✅ Sample Data
        activityList.add(
            RecentActivityModel("Purchase", "3 days ago, 10:35 PM", "cash", 1000)
        )
        activityList.add(
            RecentActivityModel("Payment Received", "2 days ago, 02:15 PM", "credit", 5000)
        )
        activityList.add(
            RecentActivityModel("Purchase", "1 day ago, 11:20 AM", "cash", 2000)
        )

        adapter = RecentActivityAdapter(this, activityList)
        binding.recyclerRecentActivities.adapter = adapter
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
