package com.example.unitedpoultry.AdminDashBoard.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateItem
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.AdminSettingModule.adapter.TodayRateAdapter
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.databinding.FragmentAdminRateManagementBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminRateManagementFragment : Fragment(), TodayRateAdapter.OnPriceChangeListener {

    private lateinit var binding: FragmentAdminRateManagementBinding

    private val viewModel: AdminSettingViewModel by viewModel()
    private var toDayRateListData = mutableListOf<TodayRateItem>()
    private lateinit var todayRateAdapter: TodayRateAdapter

    // Updated value type to Double
    private val changedRatesMap = mutableMapOf<Int, Double>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminRateManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onClick()
    }

    override fun onResume() {
        super.onResume()
        getTodayRate()
    }

    private fun onClick() {
        with(binding) {
            btnCancel.setOnClickListener {
                changedRatesMap.clear()
                getTodayRate()
            }

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

    private fun setupTodayRateAdapter() {
        val displayList = toDayRateListData.map { item ->
            if (item.product_name.equals("Tray", ignoreCase = true)) {
                val originalPrice: Double = item.price ?: 0.0

                TodayRateItem(
                    product_id = item.product_id,
                    product_name = "Petti",
                    packing = item.packing,
                    eggs_count = item.eggs_count,
                    price = originalPrice * 12.0
                )
            } else {
                item
            }
        }.toMutableList()

        todayRateAdapter = TodayRateAdapter(displayList, this)

        binding.todatRateRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = todayRateAdapter
        }
    }

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
        val priceDouble = newPrice.toDoubleOrNull() ?: return

        val originalItem = toDayRateListData.find { it.product_id == productId }

        val finalApiPrice = if (originalItem?.product_name.equals("Tray", ignoreCase = true)) {
            priceDouble / 12.0
        } else {
            priceDouble
        }

        changedRatesMap[productId] = finalApiPrice
        Log.v("RATE_CHANGE", "Product: $productId -> $finalApiPrice (Entered: $priceDouble)")
    }

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