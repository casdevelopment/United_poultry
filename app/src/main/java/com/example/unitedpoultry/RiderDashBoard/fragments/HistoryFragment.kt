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
import com.example.unitedpoultry.History.model.TransactionItem
import com.example.unitedpoultry.History.viewmodel.HistoryViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHistoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModel()

    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false
    private var currentDuration = "month"
    private var currentType = "all"

    private val transactionsList = mutableListOf<TransactionItem>()
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
        setupTypeFilters()

        highlightFilter(binding.filterMonth)
        highlightTypeFilter(binding.filterAll)

        hitHistoryApi()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(transactionsList)
        binding.historyRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            setHasFixedSize(true)
            // Pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    val lm = rv.layoutManager as LinearLayoutManager
                    val lastVisible = lm.findLastVisibleItemPosition()
                    if (!isLoading && lastVisible >= transactionsList.size - 2 && currentPage < lastPage) {
                        currentPage++
                        hitHistoryApi()
                    }
                }
            })
        }
    }

    private fun setupFilters() {
        binding.filterDay.setOnClickListener { applyFilter("today", binding.filterDay) }
        binding.filterWeek.setOnClickListener { applyFilter("week", binding.filterWeek) }
        binding.filterMonth.setOnClickListener { applyFilter("month", binding.filterMonth) }
    }

    private fun applyFilter(duration: String, selected: TextView) {
        currentDuration = duration
        highlightFilter(selected)
        resetAndLoad()
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

    private fun setupTypeFilters() {
        binding.filterAll.setOnClickListener { applyTypeFilter("all", binding.filterAll) }
        binding.filterSale.setOnClickListener { applyTypeFilter("sales", binding.filterSale) }
        binding.filterCollection.setOnClickListener { applyTypeFilter("collections", binding.filterCollection) }
        binding.filterExpense.setOnClickListener { applyTypeFilter("expenses", binding.filterExpense) }
    }

    private fun applyTypeFilter(type: String, selected: TextView) {
        currentType = type
        highlightTypeFilter(selected)
        resetAndLoad()
    }

    private fun highlightTypeFilter(selected: TextView) {
        val filters = listOf(binding.filterAll, binding.filterSale, binding.filterCollection,binding.filterExpense)
        filters.forEach {
            if (it == selected) {
                it.setTextColor(resources.getColor(android.R.color.white))
                it.setBackgroundResource(R.drawable.filter_bg_selected)
            } else {
                it.setTextColor(resources.getColor(R.color.black60))
                it.setBackgroundResource(R.drawable.filter_bg)
            }
        }
    }

    private fun resetAndLoad() {
        currentPage = 1
        lastPage = 1
        transactionsList.clear()
        adapter.notifyDataSetChanged()
        hitHistoryApi()
    }

    private fun hitHistoryApi() {
        isLoading = true
        AppUtil.startLoader(requireContext())

        viewModel.getHistory(currentPage, currentDuration, currentType)
            .observe(viewLifecycleOwner) { response ->
                when (response.status) {
                    Status.LOADING -> { /* Loader already shown */ }

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()
                        isLoading = false


                        val historyResponse = response.data?.body()
                        val body = historyResponse?.data
                        val productStatus = historyResponse?.data?.product_status

                        lastPage = body?.pagination?.last_page ?: 1


//                        body?.transactions?.let { transactions ->
//                            transactionsList.addAll(transactions)
//                            adapter.notifyItemRangeInserted(
//                                transactionsList.size - transactions.size,
//                                transactions.size
//                            )
//                        }





//                        body?.transactions?.let { transactions ->
//
//                            if (transactions.isEmpty() && transactionsList.isEmpty()) {
//
//                                showEmptyState(true)
//
//                            } else {
//
//                                showEmptyState(false)
//
//
//                                transactionsList.addAll(transactions)
//
//                                adapter.notifyItemRangeInserted(
//                                    transactionsList.size - transactions.size,
//                                    transactions.size
//                                )
//                            }
//                        }



                        val transactions = body?.transactions ?: emptyList()

                        if (transactions.isEmpty() && transactionsList.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)

                            val startPosition = transactionsList.size
                            transactionsList.addAll(transactions)

                            adapter.notifyItemRangeInserted(
                                startPosition,
                                transactions.size
                            )
                        }

                        body?.summary?.let { summary ->


                            binding.tvCashIn.text = "Rs ${summary.total_cash_in}"
                          //  binding.tvTotalTransactions.text = summary.transactions_count.toString()
                            binding.tvTotalRepaid.text = "Rs ${summary.total_repaid}"
                            binding.tvTotalBorrowed.text = "Rs ${summary.borrowed}"

                            binding.tvTotalExpense.text = "Rs ${summary.total_expenses}"


                        }

                        productStatus?.let { status ->

                            // EXPIRE
                            binding.tvExpirePeti.text = "Peti: ${status.expire.peti}"
                            binding.tvExpireTray.text = "Tray: ${status.expire.tray}"
                            binding.tvExpireSingle.text = "Eggs: ${status.expire.single}"

                            binding.tvReturnPeti.text = "Peti: ${status.return_.peti}"
                            binding.tvReturnTray.text = "Tray: ${status.return_.tray}"
                            binding.tvReturnSingle.text = "Eggs: ${status.return_.single}"

                            binding.tvLiquidKg.text = "Kg: ${status.liquid.kg}"
//                            binding.tvLiquidTray.text = "Tray: ${status.liquid.tray}"
//                            binding.tvLiquidSingle.text = "Eggs: ${status.liquid.single}"

                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        isLoading = false
                        showEmptyState(true)
                        showToast( "Network connection problem. Please try again.")
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.historyRv.visibility = if (show) View.GONE else View.VISIBLE
    }
}