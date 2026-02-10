package com.example.unitedpoultry.AdminSettingModule

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.HistoryData
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.AdminSettingModule.adapter.RateHistoryAdapter
import com.example.unitedpoultry.AdminSettingModule.adapter.TodayRateAdapter
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminRateManagmentBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.Status.*
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.mutableListOf
import kotlin.getValue


class AdminRateManagmentActivity : BaseActivity(),TodayRateAdapter.OnPriceChangeListener {

    private lateinit var binding: ActivityAdminRateManagmentBinding
    private val viewModel: AdminSettingViewModel by viewModel()
    private var historyCurrentPage = 1
    private var historyLastPage = 1
    private lateinit var historyListData: List<HistoryData>
    private var toDayRateListData = mutableListOf<TodayRateItem>()
    private var ratesList = mutableListOf<RatesData>()
    private lateinit var rateHistoryAdapter: RateHistoryAdapter
    private lateinit var todayRateAdapter: TodayRateAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminRateManagmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )




        with(binding) {


            backArrow.setOnClickListener {
                finish()
            }

            btnCancel.setOnClickListener {
                finish()
            }

            btnSave.setOnClickListener {
                updateProductRate()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        getTodayRate()
        getRateHistory()
    }


    private fun getTodayRate() {
        viewModel.todayRate().observe(this@AdminRateManagmentActivity) { serverResponse ->
            when (serverResponse.status) {
                SUCCESS -> {
                    AppUtil.stopLoader()

                    if (serverResponse.data != null && serverResponse.data.isSuccessful) {
                        val baseResponse = serverResponse.data.body()
                        // showToast(baseResponse?.message ?: "")
                        val rateDate = baseResponse?.data?.date!!
                        binding.rateDate.text = rateDate
                        toDayRateListData = baseResponse.data.rates!! as MutableList<TodayRateItem>



                        if (toDayRateListData.isNotEmpty()) {
                            setupTodayRateAdapter()
                        }

                    }

                }

                ERROR -> {
                    AppUtil.stopLoader()
                    showToast(serverResponse.message.toString())
                }

                LOADING -> {
                    AppUtil.startLoader(this@AdminRateManagmentActivity)
                }
            }

        }
    }

    private fun getRateHistory() {
        viewModel.rateHistory(historyCurrentPage)
            .observe(this@AdminRateManagmentActivity) { serverResponse ->
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
                            }

                        }

                    }

                    ERROR -> {
                        AppUtil.stopLoader()
                        showToast(serverResponse.message.toString())
                    }

                    LOADING -> {
                        AppUtil.startLoader(this@AdminRateManagmentActivity)
                    }
                }

            }
    }

    private fun setupHistoryAdapter() {
        rateHistoryAdapter = RateHistoryAdapter(ratesList)
        binding.historyRv.apply {
            this.layoutManager =
                LinearLayoutManager(this@AdminRateManagmentActivity, RecyclerView.VERTICAL, false)
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

    private fun setupTodayRateAdapter() {

        todayRateAdapter = TodayRateAdapter(toDayRateListData,this)
        binding.todatRateRv.apply {
            this.layoutManager =
                LinearLayoutManager(this@AdminRateManagmentActivity, RecyclerView.VERTICAL, false)
            this.adapter = todayRateAdapter


        }

    }

    private fun updateProductRate() {
       var updateRateModel=UpdateRateModel()
        var updateRateItem=mutableListOf<UpdateRateItem>()
       // updateRateModel.date=binding.rateDate.text.toString()
        if (toDayRateListData.isNotEmpty()) {
            for (ratesListItem in toDayRateListData) {
                if(ratesListItem.price!=0){
                    updateRateItem.add(UpdateRateItem(ratesListItem.product_id,ratesListItem.price))
                }

               // Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.product_name}"+"-- price: ${ratesListItem.price}")
            }

            val tomorrowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().apply { add(Calendar.DATE, 1) }.time)
            Log.v("tomorrowDate","tomorrow "+tomorrowDate)
            updateRateModel.date=tomorrowDate
          //  updateRateModel.date=tomorrowDate
            updateRateModel.rates=updateRateItem
            updateProductRateApi(updateRateModel)
            Log.v("updateProductRate","updateRateItem size ${updateRateItem.size}")
            Log.v("updateProductRate","updateRateModel .rates size ${updateRateModel.rates?.size}")
          //  Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.price}")
           val rates= updateRateModel.rates
            if (rates != null) {
                for (ratesListItem in rates) {
                    Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.price}")
                }
            }

        }


    }

    override fun onPriceChanged(
        position: Int,
        newPrice: String
    ) {
        toDayRateListData[position].price=newPrice.toInt()
        Log.v("updateProductRate", "newPrice : ${toDayRateListData[position].price}")
    }
//    private fun updateProductRateApi(updateRateModel: UpdateRateModel) {
//        viewModel.updateRate(updateRateModel).observe(this@AdminRateManagmentActivity){serverResponse ->
//            when (serverResponse.status) {
//                SUCCESS -> AppUtil.stopLoader()
//                ERROR -> AppUtil.stopLoader()
//                LOADING -> AppUtil.startLoader(this@AdminRateManagmentActivity)
//            }
//        }
//    }


    private fun updateProductRateApi(updateRateModel: UpdateRateModel) {
        viewModel.updateRate(updateRateModel).observe(this@AdminRateManagmentActivity) { serverResponse ->

            when (serverResponse.status) {

                LOADING -> {
                    AppUtil.startLoader(this@AdminRateManagmentActivity)
                }

                SUCCESS -> {
                    AppUtil.stopLoader()
                    val retrofitResponse = serverResponse.data

                    if (retrofitResponse != null) {
                        if (retrofitResponse.isSuccessful) {
                            // ✅ 2xx response
                            val baseResponse = retrofitResponse.body()
                            val message = baseResponse?.message ?: "Operation successful"
                            Toast.makeText(this@AdminRateManagmentActivity, message, Toast.LENGTH_SHORT).show()
                        } else {
                            // ❗ HTTP error (401, 422, 500, etc.)
                            val errorMessage = try {
                                val errorBody = retrofitResponse.errorBody()?.string()
                                if (!errorBody.isNullOrEmpty()) {
                                    val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                                    baseResponse.message ?: "Update failed"
                                } else {
                                    "Update failed"
                                }
                            } catch (e: Exception) {
                                "Update failed"
                            }
                            Toast.makeText(this@AdminRateManagmentActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AdminRateManagmentActivity, "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }

                ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        this@AdminRateManagmentActivity,
                        serverResponse.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



}
