package com.example.unitedpoultry.AdminDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminHome.Adapter.ShopVisitedAdapter
import com.example.unitedpoultry.AdminHome.model.ShopvisitedModel
import com.example.unitedpoultry.AdminHome.viewmodel.DailyPerformanceStatsViewModel
import com.example.unitedpoultry.AdminRiderModule.Adapter.AdminRidersAdapter
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderViewModel
import com.example.unitedpoultry.AdminSettingModule.SettingHomeActivity
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHomeAdminBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import com.example.unitedpoultry.rider_home.viewmodel.DailyStatsViewModel
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.getValue


class HomeAdminFragment : Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding
    private lateinit var shopAdapter: ShopVisitedAdapter
    private lateinit var adapter: AdminRidersAdapter
    private val viewModel: RiderViewModel by viewModel()
    private var riderList = mutableListOf<RiderModel>()
    private var activeRiderList = mutableListOf<RiderModel>()
    private var tempActiveRiderList = mutableListOf<RiderModel>()
    private var inActiveRiderList = mutableListOf<RiderModel>()
    private var currentPage = 1
    private var lastPage = 1

    private val ViewModel1: DailyPerformanceStatsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onclick()


        setupRecyclerView()


        binding.etSearch.addTextChangedListener {
            adapter.filter(it.toString(), "Active")

        }

    }

    override fun onResume() {
        super.onResume()
//        riderList.clear()
//        activeRiderList.clear()
        currentPage = 1
        fetchRidersFromApi(currentPage)
        loadDailyPaymentStats()
        showDate()
    }

    private fun onclick(){

        binding.profile.setOnClickListener {
            val intent = Intent(requireContext(), SettingHomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showDate(){

        binding.tvDate.text = getTodayDate()

        binding.tvDay.text = getTodayDay()
    }

    private fun fetchRidersFromApi(page: Int) {

        riderList.clear()
        activeRiderList.clear()
        inActiveRiderList.clear()


        viewModel.getRiders(page).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> if (page == 1) AppUtil.startLoader(requireContext())

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()

                    val response: Response<BaseResponse<RiderDataResponceModel>>? =
                        apiResponse.data

                    if (response != null && response.isSuccessful) {
                        val baseResponse = response.body()
                        val sellers = baseResponse?.data?.sellers
                        val pagination = baseResponse?.data?.pagination
                        if (pagination != null) lastPage = pagination.last_page

                        if (!sellers.isNullOrEmpty()) {
                            riderList.addAll(sellers)
                            for (item in sellers) {
                                if(item.is_active){
                                    activeRiderList.add(item)
                                }else{
                                    inActiveRiderList.add(item)
                                }
                            }
                            adapter.updateList(activeRiderList)
                        }

                        // Update currentPage only after success
                        currentPage = page
                        if(currentPage<lastPage){
                            fetchRidersFromApi(currentPage + 1)
                        }else{
                            binding.activeSuppliersTv.text = formatNumber(activeRiderList.size)
                            binding.pendingApprovalTv.text = formatNumber(inActiveRiderList.size)
                            binding.totalProductsTv.text = formatNumber(riderList.size)

                            if (activeRiderList.isEmpty()) {
                                binding.rvRiders.visibility = View.GONE
                                binding.layoutEmpty.visibility = View.VISIBLE
                            } else {
                                binding.rvRiders.visibility = View.VISIBLE
                                binding.layoutEmpty.visibility = View.GONE
                            }
                        }
                    }
                    else{
                        AppUtil.stopLoader()
                        binding.rvRiders.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                     AppUtil.stopLoader()


                }
            }
        }
    }

    private fun formatNumber(value: Int): String {
        return when {
            value >= 1_000_000 -> {
                val v = value / 1_000_000.0
                String.format("%.1f", v).removeSuffix(".0") + "M"
            }
            value >= 1_000 -> {
                val v = value / 1_000.0
                String.format("%.1f", v).removeSuffix(".0") + "K"
            }
            else -> value.toString()
        }
    }

    private fun formatNumber(value: Double): String {
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

    private fun setupRecyclerView() {
        adapter = AdminRidersAdapter(mutableListOf())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiders.layoutManager = layoutManager
        binding.rvRiders.adapter = adapter
        binding.rvRiders.isNestedScrollingEnabled = true // important inside NestedScrollView
    }

    private fun loadDailyPaymentStats() {

        ViewModel1.getDailyPerformanceStats().observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(requireActivity())

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<DailyPaymentStatsData>?
                        baseResponse?.data?.let { data ->

                            binding.tvCashRecieved.text = formatNumber(data.today_total_cash_received.toDouble())
                            binding.tvUnitSold.text = formatNumber(data.today_total_eggs_sold)
                           // binding.tvUnitDelieved.text = formatNumber(data.today_total_eggs_sold)
                            binding.tvEggReturned.text = formatNumber(data.today_total_eggs_returned)
                            binding.tvTotalShops.text = formatNumber(data.total_shops)


                            binding.tvEggPicked.text = formatNumber(data.today_total_eggs_picked)
                            binding.tvWastedEggs.text = formatNumber(data.today_total_eggs_waste)




                            val totalShops = data.total_shops
                            val visitedShops = data.today_total_visited_shops

                            val visitedPercentage = if (totalShops > 0) {
                                (visitedShops * 100) / totalShops
                            } else {
                                0
                            }

                            binding.tVisitedShops.text = "$visitedPercentage%"
                            binding.pVisitedShops.progress = visitedPercentage

                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        requireContext(),
                        response.message ?: "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTodayDay(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }



}
