package com.example.unitedpoultry.AdminReportModule.RiderReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminReportModule.RiderReport.Adapter.RiderReportAdapter
import com.example.unitedpoultry.AdminReportModule.RiderReport.model.RiderReportModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminRiderReportBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminRiderReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRiderReportBinding
    private lateinit var adapter: RiderReportAdapter

    private val allRecords = mutableListOf<RiderReportModel>()
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRiderReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        setupRecycler()
        setupFilters()

        binding.backArrow.setOnClickListener {
            finish()
        }
        // ✅ Default Month Selected
        applyMonthFilter()
    }

    private fun setupRecycler() {
        allRecords.addAll(
            listOf(
                RiderReportModel("Ahmed Hassan", "Rs. 386,000", "Rs. 320,000", "92%", 3, 40, "2025-12-28"),
                RiderReportModel("Ali Raza", "Rs. 210,000", "Rs. 190,000", "90%", 2, 28, "2025-12-26"),
                RiderReportModel("Usman Khan", "Rs. 150,000", "Rs. 130,000", "87%", 4, 35, "2025-12-12"),
                RiderReportModel("Salman", "Rs. 410,000", "Rs. 390,000", "95%", 5, 52, "2025-11-20")
            )
        )

        adapter = RiderReportAdapter(allRecords.toMutableList())

        binding.rvAreas.apply {
            layoutManager = LinearLayoutManager(this@AdminRiderReportActivity)
            adapter = this@AdminRiderReportActivity.adapter
        }
    }

    private fun setupFilters() {
        binding.filterDay.setOnClickListener {
            adapter.updateList(allRecords.filter { isToday(it.date) })
            updateFilterUI(binding.filterDay)
        }

        binding.filterWeek.setOnClickListener {
            adapter.updateList(allRecords.filter { isThisWeek(it.date) })
            updateFilterUI(binding.filterWeek)
        }

        binding.filterMonth.setOnClickListener {
            applyMonthFilter()
        }
    }

    private fun applyMonthFilter() {
        adapter.updateList(allRecords.filter { isThisMonth(it.date) })
        updateFilterUI(binding.filterMonth)
    }

    // 📅 DATE HELPERS
    private fun isToday(dateStr: String): Boolean {
        val date = formatter.parse(dateStr) ?: return false
        val cal = Calendar.getInstance().apply { time = date }
        val now = Calendar.getInstance()
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(dateStr: String): Boolean {
        val date = formatter.parse(dateStr) ?: return false
        val cal = Calendar.getInstance().apply { time = date }
        val now = Calendar.getInstance()
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isThisMonth(dateStr: String): Boolean {
        val date = formatter.parse(dateStr) ?: return false
        val cal = Calendar.getInstance().apply { time = date }
        val now = Calendar.getInstance()
        return cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
    }

    private fun updateFilterUI(selected: android.widget.TextView) {
        val filters = listOf(binding.filterDay, binding.filterWeek, binding.filterMonth)

        filters.forEach {
            it.setBackgroundResource(R.drawable.filter_bg_round)
            it.setTextColor(ContextCompat.getColor(this, R.color.black60))
        }

        selected.setBackgroundResource(R.drawable.filter_bg_round_selected)
        selected.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
}
