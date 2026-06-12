package com.example.unitedpoultry.AdminSettingModule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.HistoryData
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.AdminSettingModule.adapter.RateHistoryAdapter
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminRateHistoryBinding
import com.example.unitedpoultry.network.Status.*
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.mutableListOf
import kotlin.getValue
import androidx.core.widget.addTextChangedListener


class AdminRateHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRateHistoryBinding
    private val viewModel: AdminSettingViewModel by viewModel()
    private var historyCurrentPage = 1
    private var historyLastPage = 1
    private lateinit var historyListData: List<HistoryData>
    private var ratesList = mutableListOf<RatesData>()
    private lateinit var rateHistoryAdapter: RateHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminRateHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        setupSearch()


        with(binding) {


            backArrow.setOnClickListener {
                finish()
            }

        }


    }

    override fun onResume() {
        super.onResume()
        getRateHistory()
    }




    private fun getRateHistory() {
        viewModel.rateHistory(historyCurrentPage)
            .observe(this@AdminRateHistoryActivity) { serverResponse ->
                when (serverResponse.status) {
                    SUCCESS -> {
                        AppUtil.stopLoader()

                        if (serverResponse.data != null && serverResponse.data.isSuccessful) {
                            val baseResponse = serverResponse.data.body()
                            // showToast(baseResponse?.message ?: "")
                            historyListData = baseResponse?.data?.history!!
                            val pagination = baseResponse.data.pagination!!
                            historyLastPage = pagination.last_page


                            if (historyListData.isNotEmpty()) {

                                binding.layoutEmpty.visibility = View.GONE
                                binding.historyRv.visibility = View.VISIBLE


                                for (ratesItem in historyListData) {
                                    val date = ratesItem.date
                                    for (item in ratesItem.rates!!) {
                                        ratesList.add(
                                            RatesData(
                                                id = item.id,
                                                product_id = item.product_id,
                                                product_name = item.product_name,
                                                packing = item.packing,
                                                eggs_count = item.eggs_count,
                                                price = item.price,
                                                date = date
                                            )
                                        )
                                    }
                                }
                                if (ratesList.isNotEmpty()) setupHistoryAdapter()
                            }else {

                                if (historyCurrentPage == 1) {
                                    binding.layoutEmpty.visibility = View.VISIBLE
                                    binding.historyRv.visibility = View.GONE
                                }
                            }

                        }

                    }

                    ERROR -> {
                        AppUtil.stopLoader()
                        showToast("Network Error")
                        if (historyCurrentPage == 1) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.historyRv.visibility = View.GONE
                        }
                    }

                    LOADING -> {
                        AppUtil.startLoader(this@AdminRateHistoryActivity)
                    }
                }

            }
    }

    private fun setupHistoryAdapter() {
        rateHistoryAdapter = RateHistoryAdapter(ratesList)
        binding.historyRv.apply {
            this.layoutManager =
                LinearLayoutManager(this@AdminRateHistoryActivity, RecyclerView.VERTICAL, false)
            this.adapter = rateHistoryAdapter
            // Add scroll listener to detect when last item is reached
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Check if we've reached the last item
                    if (lastVisibleItemPosition >= totalItemCount - 1) {
                        // Load more data
                        if (historyCurrentPage < historyLastPage) {
                            loadMoreHistoryData()
                        }

                    }

                    // Optional: Log for debugging
                    Log.v(
                        "ScrollInfo",
                        "First: $firstVisibleItemPosition, Last: $lastVisibleItemPosition, Total: $totalItemCount"
                    )
                }
            })
        }

    }


    private fun loadMoreHistoryData() {
        // Increment page and fetch more data
        historyCurrentPage++
        getRateHistory()
    }


    private fun setupSearch() {

        binding.etSearch.addTextChangedListener { editable ->

            val query = editable.toString().trim()

            rateHistoryAdapter.filter(query)

            if (rateHistoryAdapter.itemCount == 0) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.historyRv.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.historyRv.visibility = View.VISIBLE
            }
        }
    }
}
