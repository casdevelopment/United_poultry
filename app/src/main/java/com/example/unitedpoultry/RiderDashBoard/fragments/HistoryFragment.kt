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
    private var isLoading = false
    private var currentDuration = "month"

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

        loadSaleHistory()
    }

    // -------------------- RecyclerView --------------------

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(salesList)

        binding.historyRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)

                    val lm = rv.layoutManager as LinearLayoutManager
                    val lastVisible = lm.findLastVisibleItemPosition()

                    if (!isLoading && lastVisible >= lm.itemCount - 2 && currentPage < lastPage) {
                        currentPage++
                        loadSaleHistory()
                    }
                }
            })
        }
    }

    // -------------------- Filters --------------------

    private fun setupFilters() {
        binding.filterDay.setOnClickListener {
            applyFilter("today", binding.filterDay)
        }

        binding.filterWeek.setOnClickListener {
            applyFilter("week", binding.filterWeek)
        }

        binding.filterMonth.setOnClickListener {
            applyFilter("month", binding.filterMonth)
        }
    }

    private fun applyFilter(duration: String, selected: TextView) {
        currentDuration = duration
        highlightFilter(selected)
        refreshSales()
    }

    private fun highlightFilter(selected: TextView) {
        val filters = listOf(binding.filterDay, binding.filterWeek, binding.filterMonth)
        filters.forEach {
            if (it == selected) {
                it.setTextColor(resources.getColor(android.R.color.white))
                it.setBackgroundResource(R.drawable.filter_bg_round_selected)
            } else {
                it.setTextColor(resources.getColor(R.color.black60))
                it.setBackgroundResource(R.drawable.filter_bg_round)
            }
        }
    }

    // -------------------- API --------------------

    private fun refreshSales() {
        currentPage = 1
        lastPage = 1
        salesList.clear()
        adapter.notifyDataSetChanged()
        loadSaleHistory()
    }

    private fun loadSaleHistory() {
        isLoading = true

        viewModel.getSaleHistory(currentPage, currentDuration)
            .observe(viewLifecycleOwner) { response ->
                when (response.status) {

                    Status.LOADING -> AppUtil.startLoader(requireContext())

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()
                        isLoading = false

                        val body = response.data?.body()?.data
                        lastPage = body?.pagination?.last_page ?: 1

                        body?.sales?.let {
                            salesList.addAll(it)
                            adapter.notifyItemRangeInserted(
                                salesList.size - it.size,
                                it.size
                            )
                            updateSummary()
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        isLoading = false
                        showToast(response.message ?: "Failed to load sales")
                    }
                }
            }
    }

    // -------------------- Summary --------------------

    private fun updateSummary() {
        val totalSales = salesList.sumOf {
            it.total.toDoubleOrNull() ?: 0.0
        }

        val totalCollected = salesList.sumOf {
            it.cash_received.toDoubleOrNull() ?: 0.0
        }

        binding.tvTotalSales.text = "Rs ${formatAmount(totalSales)}"
        binding.tvTotalCollected.text = "Rs ${formatAmount(totalCollected)}"
        binding.tvTotalTransactions.text = salesList.size.toString()
    }

    private fun formatAmount(value: Double): String {
        return when {
            value >= 1_000_000 -> "${(value / 1_000_000).format()}M"
            value >= 1_000 -> "${(value / 1_000).format()}K"
            else -> value.toInt().toString()
        }
    }

    private fun Double.format(): String =
        String.format("%.1f", this).removeSuffix(".0")

    // --------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
