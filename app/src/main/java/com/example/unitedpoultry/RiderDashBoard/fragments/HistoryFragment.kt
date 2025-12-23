package com.example.unitedpoultry.RiderDashBoard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.History.Adapter.HistoryAdapter
import com.example.unitedpoultry.History.model.HistoryModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    private var selectedType = "All"
    private var selectedTime = "Today"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyList = listOf(
            HistoryModel("Jalal Sons", "Sale . 5 Boxes . Credit", "Sale", 17000, 0, "10:45 AM"),
            HistoryModel("Al-Fatah Store", "Collection . Cash", "Collection", 12000, 2, "11:10 AM"),
            HistoryModel("Green Valley Mart", "Collection . Cash", "Collection", 20000, 5, "12:30 PM"),
            HistoryModel("Mini Mart Central", "Sale . 9 Boxes . Credit", "Sale", 15000, 10, "09:20 AM")
        )

        historyAdapter = HistoryAdapter(historyList.toMutableList())
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = historyAdapter

        // Default selection
        selectTopFilter(binding.filterAll)
        selectBottomFilter(binding.filterMonth)

        // TOP FILTERS
        binding.filterAll.setOnClickListener {
            selectedType = "All"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectTopFilter(binding.filterAll)
        }

        binding.filterVisited.setOnClickListener {
            selectedType = "Sale"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectTopFilter(binding.filterVisited)
        }

        binding.filterPending.setOnClickListener {
            selectedType = "Collection"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectTopFilter(binding.filterPending)
        }

        // BOTTOM FILTERS
        binding.filterDay.setOnClickListener {
            selectedTime = "Today"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectBottomFilter(binding.filterDay)
        }

        binding.filterWeek.setOnClickListener {
            selectedTime = "Week"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectBottomFilter(binding.filterWeek)
        }

        binding.filterMonth.setOnClickListener {
            selectedTime = "Month"
            historyAdapter.applyFilter(selectedType, selectedTime)
            selectBottomFilter(binding.filterMonth)
        }
    }

    // 🔹 TOP FILTER UI
    private fun selectTopFilter(selected: TextView) {
        val all = listOf(
            binding.filterAll,
            binding.filterVisited,
            binding.filterPending
        )

        all.forEach {
            it.setBackgroundResource(R.drawable.filter_bg)
            it.setTextColor(resources.getColor(R.color.black60))
        }

        selected.setBackgroundResource(R.drawable.filter_bg_selected)
        selected.setTextColor(resources.getColor(R.color.white))
    }

    // 🔹 BOTTOM FILTER UI
    private fun selectBottomFilter(selected: TextView) {
        val all = listOf(
            binding.filterDay,
            binding.filterWeek,
            binding.filterMonth
        )

        all.forEach {
            it.setBackgroundResource(R.drawable.filter_bg_round)
            it.setTextColor(resources.getColor(R.color.black60))
        }

        selected.setBackgroundResource(R.drawable.filter_bg_round_selected)
        selected.setTextColor(resources.getColor(R.color.white))
    }
}
