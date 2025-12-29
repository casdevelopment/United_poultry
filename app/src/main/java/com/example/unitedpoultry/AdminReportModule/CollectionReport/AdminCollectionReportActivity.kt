package com.example.unitedpoultry.AdminReportModule.CollectionReport

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminReportModule.CollectionReport.Adapter.CollectionRecordAdapter
import com.example.unitedpoultry.AdminReportModule.CollectionReport.model.CollectionRecordModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminCollectionReportBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminCollectionReportActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminCollectionReportBinding
    private lateinit var adapter: CollectionRecordAdapter

    private val allRecords = mutableListOf<CollectionRecordModel>()
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCollectionReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        setupRecyclerView()
        setupFilters()
        adapter.updateList(allRecords.filter { isThisMonth(it.date) })
        updateFilterUI(binding.filterMonth)

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {

        allRecords.addAll(
            listOf(
                CollectionRecordModel(
                    "Jalal Sons - Gulberg",
                    "10:45 AM • Ahmed Hassan",
                    "Cash",
                    "Invoice #1243",
                    "Rs. 15,000",
                    "2025-12-28"
                ),
                CollectionRecordModel(
                    "Al-Fatah Store",
                    "11:30 AM • Ali Raza",
                    "Credit",
                    "Invoice #1244",
                    "Rs. 8,500",
                    "2025-12-25"
                ),
                CollectionRecordModel(
                    "Green Valley Mart",
                    "01:15 PM • Usman Khan",
                    "Cheque",
                    "Invoice #1245",
                    "Rs. 12,000",
                    "2025-12-12"
                ),
                CollectionRecordModel(
                    "Metro Cash & Carry",
                    "02:40 PM • Salman",
                    "Cash",
                    "Invoice #1246",
                    "Rs. 21,000",
                    "2025-12-28"
                )
            )
        )

        adapter = CollectionRecordAdapter(allRecords.toMutableList())

        binding.rvAreas.apply {
            layoutManager = LinearLayoutManager(this@AdminCollectionReportActivity)
            adapter = this@AdminCollectionReportActivity.adapter
        }
    }

    // 🔹 FILTER HANDLERS
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
            adapter.updateList(allRecords.filter { isThisMonth(it.date) })
            updateFilterUI(binding.filterMonth)
        }
    }

    // 🔹 DATE CHECKS (STRING BASED)
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

    // 🔹 UI SELECTION
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
