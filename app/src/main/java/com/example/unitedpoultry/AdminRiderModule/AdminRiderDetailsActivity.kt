package com.example.unitedpoultry.AdminRiderModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminRiderModule.Adapter.RiderAssignedAreasAdapter
import com.example.unitedpoultry.AdminRiderModule.model.AdminAssignedAreasModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminRiderDetailsBinding


class AdminRiderDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRiderDetailsBinding
    private lateinit var adapter: RiderAssignedAreasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRiderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        binding.backArrow.setOnClickListener { finish() }


        val initial = intent.getStringExtra("initial") ?: "N/A"
        val name = intent.getStringExtra("RIDER_NAME") ?: "N/A"
        val status = intent.getStringExtra("STATUS") ?: "N/A"
        val areas = intent.getIntExtra("AREAS", 0)
        val shops = intent.getIntExtra("SHOPS", 0)
        val sale = intent.getIntExtra("SALE", 0)

        // Set data
        binding.tvInitials.text = initial
        binding.tvRiderName.text = name
        binding.tvStatus.text = status
        binding.tvShopsVisited.text = areas.toString()
        binding.tvShops.text = shops.toString()
        binding.tvTodaySale.text = "Rs. $sale"

        binding.performance.progress = 70

        binding.btnManageAreas.setOnClickListener {
            val intent = Intent(this, AdminAssignAreasActivity::class.java)

            // Pass the rider name
            intent.putExtra("name", name)
            intent.putExtra("initial", initial)

            startActivity(intent)
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AdminEditRiderActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }




        // Sample data
        val data = mutableListOf(
            AdminAssignedAreasModel("Gulberg", "15 shops assigned"),
            AdminAssignedAreasModel("Model Town", "12 shops assigned"),
            AdminAssignedAreasModel("DHA Phase 5", "13 shops assigned")
        )

        adapter = RiderAssignedAreasAdapter(data)

        binding.rvAreas.layoutManager = LinearLayoutManager(this)
        binding.rvAreas.adapter = adapter


        binding.assignedShopsCount.text = "Assigned Areas (${data.count()})"


    }
}
