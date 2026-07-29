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
import com.example.unitedpoultry.History.model.ProductStatus
import com.example.unitedpoultry.History.model.TransactionItem
import com.example.unitedpoultry.History.viewmodel.HistoryViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHistoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.rider_home.adapter.RiderHomeStatsAdapter
import com.example.unitedpoultry.rider_home.model.PickedTodayProduct
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


    private lateinit var expireAdapter: RiderHomeStatsAdapter
    private lateinit var returnAdapter: RiderHomeStatsAdapter

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
        setupDamageEggsRecyclerViews()
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

    private fun setupDamageEggsRecyclerViews() {

        expireAdapter = RiderHomeStatsAdapter()

        binding.rvExpire.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = expireAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }



        returnAdapter = RiderHomeStatsAdapter()

        binding.rvReturn.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = returnAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
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
                        if (productStatus != null) {
                            bindPickedItemsData(productStatus)
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


    private fun toggleSection(
        recyclerView: View,
        emptyView: View,
        hasData: Boolean
    ) {
        recyclerView.visibility = if (hasData) View.VISIBLE else View.GONE
        emptyView.visibility = if (hasData) View.GONE else View.VISIBLE
    }


    private fun bindPickedItemsData(data: ProductStatus) {

        val rawExpireList = data.expire.map { it.key to it.value }
        val rawReturnList = data.`return`.map { it.key to it.value }

        // Convert raw Tray counts into Pettis + Remaining Trays
        val expireList = convertTraysToPettiAndTrays(rawExpireList)
        val returnList = convertTraysToPettiAndTrays(rawReturnList)

        binding.tvLiquidQuantity.text = data.liquid.kg.toString()

        expireAdapter.submitList(expireList)
        returnAdapter.submitList(returnList)

        toggleSection(binding.rvExpire, binding.emptyExpire, expireList.isNotEmpty())
        toggleSection(binding.rvReturn, binding.emptyReturn, returnList.isNotEmpty())
    }


    private fun convertTraysToPettiAndTrays(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        var pettiCount = 0

        for ((key, count) in list) {
            if (count <= 0) continue

            if (key.contains("tray", ignoreCase = true)) {
                pettiCount += count / 12
                val remainingTrays = count % 12

                if (remainingTrays > 0) {
                    result.add(key to remainingTrays)
                }
            } else {
                result.add(key to count)
            }
        }

        if (pettiCount > 0) {
            result.add(0, "Petti" to pettiCount)
        }

        return result
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