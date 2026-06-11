package com.example.unitedpoultry.AdminDashBoard.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminArea.Adapter.AdminAreaAdapter
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminArea.viewmodel.AreaViewModel
import com.example.unitedpoultry.AdminSettingModule.DataModel.HistoryData
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.AdminSettingModule.adapter.RateHistoryAdapter
import com.example.unitedpoultry.AdminSettingModule.adapter.TodayRateAdapter
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.databinding.FragmentAdminRateManagementBinding
import com.example.unitedpoultry.databinding.FragmentAreasAdminBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminRateManagementFragment : Fragment(), TodayRateAdapter.OnPriceChangeListener {

    private lateinit var binding: FragmentAdminRateManagementBinding

    private val viewModel: AdminSettingViewModel by viewModel()
//    private var historyCurrentPage = 1
//    private var historyLastPage = 1
//    private lateinit var historyListData: List<HistoryData>
    private var toDayRateListData = mutableListOf<TodayRateItem>()
//    private var ratesList = mutableListOf<RatesData>()
//    private lateinit var rateHistoryAdapter: RateHistoryAdapter
    private lateinit var todayRateAdapter: TodayRateAdapter


    private val changedRatesMap = mutableMapOf<Int, Int>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminRateManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onClick()
    }


    override fun onResume() {
        super.onResume()
        getTodayRate()
       // getRateHistory()

    }

    private fun onClick(){
        with(binding) {


//            backArrow.setOnClickListener {
//                finish()
//            }
//
//            btnCancel.setOnClickListener {
//                finish()
//            }

            btnSave.setOnClickListener {
                updateProductRate()
            }
        }

    }


    private fun getTodayRate() {
        viewModel.todayRate().observe(viewLifecycleOwner) { serverResponse ->
            when (serverResponse.status) {
                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    if (serverResponse.data != null && serverResponse.data.isSuccessful) {
                        val baseResponse = serverResponse.data.body()
                        val rateDate = baseResponse?.data?.date ?: ""
                        binding.rateDate.text = rateDate

                        toDayRateListData = baseResponse?.data?.rates?.toMutableList() ?: mutableListOf()

                        if (toDayRateListData.isNotEmpty()) {
                            binding.todatRateRv.visibility = View.VISIBLE
                            binding.layoutEmpty.visibility = View.GONE
                            setupTodayRateAdapter()
                        } else {
                            binding.todatRateRv.visibility = View.GONE
                            binding.layoutEmpty.visibility = View.VISIBLE
                        }
                    } else {
                        binding.todatRateRv.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    binding.todatRateRv.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    showToast(serverResponse.message.toString())
                }

                Status.LOADING -> {
                    AppUtil.startLoader(requireContext())
                }
            }
        }
    }


//    private fun getRateHistory() {
//        viewModel.rateHistory(historyCurrentPage).observe(viewLifecycleOwner) { serverResponse ->
//            when (serverResponse.status) {
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//
//                    if (serverResponse.data != null && serverResponse.data.isSuccessful) {
//                        val baseResponse = serverResponse.data.body()
//                        historyListData = baseResponse?.data?.history ?: emptyList()
//                        val pagination = baseResponse?.data?.pagination
//                        historyLastPage = pagination?.last_page ?: 1
//
//                        ratesList.clear() // clear previous data
//
//                        if (historyListData.isNotEmpty()) {
//                            for (ratesItem in historyListData) {
//                                val date = ratesItem.date
//                                for (item in ratesItem.rates ?: emptyList()) {
//                                    ratesList.add(
//                                        RatesData(
//                                            id = item.id,
//                                            product_id = item.product_id,
//                                            product_name = item.product_name,
//                                            packing = item.packing,
//                                            eggs_count = item.eggs_count,
//                                            price = item.price,
//                                            date = date
//                                        )
//                                    )
//                                }
//                            }
//
//                            if (ratesList.isNotEmpty()) {
//                                setupHistoryAdapter()
//                                binding.layoutEmptyHistory.visibility = View.GONE
//                                binding.historyLayout.visibility = View.VISIBLE
//                            } else {
//                                // No rate items
//                                binding.layoutEmptyHistory.visibility = View.VISIBLE
//                                binding.historyLayout.visibility = View.GONE
//                            }
//
//                        } else {
//                            // No history data
//                            binding.layoutEmptyHistory.visibility = View.VISIBLE
//                            binding.historyLayout.visibility = View.GONE
//                        }
//                    }
//                }
//
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    showToast(serverResponse.message.toString())
//                    binding.layoutEmptyHistory.visibility = View.VISIBLE
//                    binding.historyLayout.visibility = View.GONE
//                }
//
//                Status.LOADING -> AppUtil.startLoader(requireContext())
//            }
//        }
//    }


//    private fun setupHistoryAdapter() {
//        rateHistoryAdapter = RateHistoryAdapter(ratesList)
//        binding.historyRv.apply {
//            this.layoutManager =
//                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//            this.adapter = rateHistoryAdapter
//            // Add scroll listener to detect when last item is reached
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    val totalItemCount = layoutManager.itemCount
//                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//                    // Check if we've reached the last item
//                    if (lastVisibleItemPosition >= totalItemCount - 1) {
//                        // Load more data
//                        if (historyCurrentPage < historyLastPage) {
//                            loadMoreHistoryData()
//                        }
//
//                    }
//
//                    // Optional: Log for debugging
//                    Log.v(
//                        "ScrollInfo",
//                        "First: $firstVisibleItemPosition, Last: $lastVisibleItemPosition, Total: $totalItemCount"
//                    )
//                }
//            })
//        }
//
//    }


//    private fun loadMoreHistoryData() {
//        // Increment page and fetch more data
//        historyCurrentPage++
//        getRateHistory()
//    }

    private fun setupTodayRateAdapter() {

        todayRateAdapter = TodayRateAdapter(toDayRateListData,this)
        binding.todatRateRv.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = todayRateAdapter


        }

    }

//    private fun updateProductRate() {
//        var updateRateModel= UpdateRateModel()
//        var updateRateItem=mutableListOf<UpdateRateItem>()
//        // updateRateModel.date=binding.rateDate.text.toString()
//        if (toDayRateListData.isNotEmpty()) {
//            for (ratesListItem in toDayRateListData) {
//                if(ratesListItem.price!=0){
//                    updateRateItem.add(UpdateRateItem(ratesListItem.product_id,ratesListItem.price))
//                }
//
//                // Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.product_name}"+"-- price: ${ratesListItem.price}")
//            }
//
//            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
//            //Log.v("tomorrowDate","tomorrow "+tomorrowDate)
//            updateRateModel.date=todayDate
//            //  updateRateModel.date=tomorrowDate
//            updateRateModel.rates=updateRateItem
//            updateProductRateApi(updateRateModel)
//            Log.v("updateProductRate","updateRateItem size ${updateRateItem.size}")
//            Log.v("updateProductRate","updateRateModel .rates size ${updateRateModel.rates?.size}")
//            //  Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.price}")
//            val rates= updateRateModel.rates
//            if (rates != null) {
//                for (ratesListItem in rates) {
//                    Log.v("updateProductRate","product id: ${ratesListItem.product_id}"+ "product Name: ${ratesListItem.price}")
//                }
//            }
//
//        }
//
//
//    }


    private fun updateProductRate() {

        if (changedRatesMap.isEmpty()) {
            Toast.makeText(requireContext(), "No changes to update", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRateItem = mutableListOf<UpdateRateItem>()

        for ((productId, price) in changedRatesMap) {
            updateRateItem.add(
                UpdateRateItem(
                    product_id = productId,
                    price = price
                )
            )
        }

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Calendar.getInstance().time)

        val updateRateModel = UpdateRateModel().apply {
            date = todayDate
            rates = updateRateItem
        }

        Log.v("UPDATE_API", "Sending ${updateRateItem.size} changed items")

        updateProductRateApi(updateRateModel)
    }





    override fun onPriceChanged(productId: Int, newPrice: String) {
        val priceInt = newPrice.toIntOrNull() ?: return

        changedRatesMap[productId] = priceInt

        Log.v("RATE_CHANGE", "Product: $productId -> $priceInt")
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
        viewModel.updateRate(updateRateModel).observe(viewLifecycleOwner) { serverResponse ->

            when (serverResponse.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(requireContext())
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val retrofitResponse = serverResponse.data

                    if (retrofitResponse != null) {
                        if (retrofitResponse.isSuccessful) {

                            val baseResponse = retrofitResponse.body()
                            val message = baseResponse?.message ?: "Operation successful"
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        } else {

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
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No response from server", Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        requireContext(),
                        serverResponse.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }





}
