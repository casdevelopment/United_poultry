package com.example.unitedpoultry.RiderDashBoard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.Adapter.HistoryAdapter
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.History.viewmodel.RiderSaleHistoryViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHistoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RiderSaleHistoryViewModel by viewModel()

    private var currentPage = 1
    private var lastPage = 1
    private var currentDuration = "month" // default

    private val salesList = mutableListOf<SaleItem>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilters()
        highlightFilter(binding.filterMonth)
    }

    override fun onResume() {
        super.onResume()

        loadSaleHistory()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(salesList)
        binding.historyRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    val layoutManager = rv.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    if (lastVisible >= totalItemCount - 1 && currentPage < lastPage) {
                        currentPage++
                        loadSaleHistory()
                    }
                }
            })
        }
    }

    private fun setupFilters() {
        binding.filterDay.setOnClickListener {
            currentDuration = "today"
            highlightFilter(binding.filterDay)
            refreshSales()
        }
        binding.filterWeek.setOnClickListener {
            currentDuration = "week"
            highlightFilter(binding.filterWeek)
            refreshSales()
        }
        binding.filterMonth.setOnClickListener {
            currentDuration = "month"
            highlightFilter(binding.filterMonth)
            refreshSales()
        }
    }

    private fun highlightFilter(selected: TextView) {
        val filters = listOf(binding.filterDay, binding.filterWeek, binding.filterMonth)
        for (filter in filters) {
            if (filter == selected) {
                filter.setTextColor(resources.getColor(android.R.color.white))
                filter.setBackgroundResource(R.drawable.filter_bg_round_selected)
            } else {
                filter.setTextColor(resources.getColor(R.color.black60))
                filter.setBackgroundResource(R.drawable.filter_bg_round)
            }
        }
    }

    private fun refreshSales() {
        currentPage = 1
        lastPage = 1
        salesList.clear()
        adapter.notifyDataSetChanged()
        loadSaleHistory()
    }

    private fun loadSaleHistory() {
        viewModel.getSaleHistory(currentPage, currentDuration).observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val body = response.data?.body()?.data
                    lastPage = body?.pagination?.last_page ?: 1

                    body?.sales?.let {
                        salesList.addAll(it)
                        adapter.notifyDataSetChanged()
                        updateSummary() // 🔥 HERE
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    showToast(response.message ?: "Failed to load sales")
                }
                Status.LOADING -> AppUtil.startLoader(requireContext())
            }
        }
    }

    private fun updateSummary() {
        val totalAmount = salesList.sumOf {
            it.total.toDoubleOrNull() ?: 0.0
        }

        val totalAmountCollected = salesList.sumOf {
            it.cash_received.toDoubleOrNull() ?: 0.0
        }

        binding.tvTotalSales.text = "Rs. ${formatAmount(totalAmount)}"
        binding.tvTotalCollected.text = "Rs. ${formatAmount(totalAmountCollected)}"
        binding.tvTotalTransactions.text = salesList.size.toString()
    }


    private fun formatAmount(value: Double): String {
        return when {
            value >= 1_000_000 -> {
                val v = value / 1_000_000
                String.format("%.1f", v).removeSuffix(".0") + "M"
            }
            value >= 1_000 -> {
                val v = value / 1_000
                String.format("%.1f", v).removeSuffix(".0") + "K"
            }
            else -> value.toInt().toString()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
