package com.example.unitedpoultry.AdminDashBoard.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminRiderModule.model.AdminRiderModel
import com.example.unitedpoultry.AdminRiderModule.Adapter.AdminRidersAdapter
import com.example.unitedpoultry.AdminRiderModule.AdminAddNewRiderActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentRiderAdminBinding


class RiderAdminFragment : Fragment() {

    private lateinit var binding: FragmentRiderAdminBinding
    private lateinit var adapter: AdminRidersAdapter
    private var currentFilter = "ALL"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiderAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AdminAddNewRiderActivity::class.java)
            startActivity(intent)
        }

        // Dummy data
        val areaList = mutableListOf(
            AdminRiderModel("Ahmed Hassan", "15 Mar 2024", "Active", 17, 7, 95000),
            AdminRiderModel("Malik Ahmed", "20 Jan 2024", "Offline", 12, 5, 45000),
            AdminRiderModel("Ahsan Ali", "10 Apr 2024", "Active", 20, 9, 120000),
            AdminRiderModel("Muhammad Asim", "02 Feb 2024", "Suspended", 8, 2, 10000)
        )

        // Setup RecyclerView
        adapter = AdminRidersAdapter(areaList)
        binding.rvRiders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiders.adapter = adapter

        updateFilterCounts()
        highlightFilter("ALL")

        binding.etSearch.addTextChangedListener {
            adapter.filter(it.toString(), currentFilter)
        }

        binding.filterAll.setOnClickListener { applyFilter("ALL") }
        binding.filterActive.setOnClickListener { applyFilter("Active") }
        binding.filterOffline.setOnClickListener { applyFilter("Offline") }
        binding.filterSuspended.setOnClickListener { applyFilter("Suspended") }

        // Show total areas
  //      binding.tvAreasCount.text = "${areaList.size} areas"

        // Search functionality
//        binding.etSearch.addTextChangedListener { editable ->
//            adapter.filter(editable.toString())
//        }
//        binding.fabAdd.setOnClickListener {
//            val intent = Intent(requireContext(), AddNewAreaActivity::class.java)
//            startActivity(intent)
//        }

    }

    private fun applyFilter(type: String) {
        currentFilter = type
        adapter.filter(binding.etSearch.text.toString(), type)
        highlightFilter(type)
    }

    private fun updateFilterCounts() {
        binding.filterAll.text = "All (${adapter.countByStatus("ALL")})"
        binding.filterActive.text = "Active (${adapter.countByStatus("Active")})"
        binding.filterOffline.text = "Offline (${adapter.countByStatus("Offline")})"
        binding.filterSuspended.text = "Suspended (${adapter.countByStatus("Suspended")})"
    }

    private fun highlightFilter(type: String) {
        val allFilters = listOf(binding.filterAll, binding.filterActive, binding.filterOffline, binding.filterSuspended)
        allFilters.forEach {
            it.setBackgroundResource(R.drawable.filter_bg)
            it.setTextColor(Color.BLACK)
        }

        val selected = when(type) {
            "Active" -> binding.filterActive
            "Offline" -> binding.filterOffline
            "Suspended" -> binding.filterSuspended
            else -> binding.filterAll
        }

        selected.setBackgroundResource(R.drawable.filter_bg_selected)
        selected.setTextColor(Color.WHITE)
    }
}
