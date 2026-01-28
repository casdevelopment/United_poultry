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

        val historyList = listOf(
            ShopvisitedModel("Jalal Sons", "Last visit: 3 days ago. Rs. 12500. 12 orders", R.drawable.visitedshopimage1),
            ShopvisitedModel("Al-Fatah Store", "Last visit: 5 days ago. Cash collection", R.drawable.visitedshopimage2),
            ShopvisitedModel("Green Valley Mart", "Last visit: 2 days ago. Cash collection", R.drawable.visitedshopimage1),
            ShopvisitedModel("Mini Mart Central", "Last visit: 1 day ago. 9 Boxes. Credit", R.drawable.visitedshopimage2)
        )

        shopAdapter = ShopVisitedAdapter(historyList.toMutableList())
        binding.rvVisitedShops.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisitedShops.adapter = shopAdapter

        val assignedShops = 40
        val visitedShops = 30
        val remainingShops = 10

        binding.tvAssignedShops.text = "$assignedShops%"
        binding.pAssignedshops.progress = assignedShops

        binding.tVisitedShops.text = "$visitedShops%"
        binding.pVisitedShops.progress = visitedShops

        binding.tvRemainingShops.text = "$remainingShops%"
        binding.pRemainingShops.progress = remainingShops



        setupRecyclerView()
        fetchRidersFromApi(currentPage)
        binding.etSearch.addTextChangedListener {
            adapter.filter(it.toString(), "Active")

        }

    }

    override fun onResume() {
        super.onResume()
        loadDailyPaymentStats()
    }

    private fun onclick(){

        binding.profile.setOnClickListener {
            val intent = Intent(requireContext(), SettingHomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchRidersFromApi(page: Int) {
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
                            binding.activeSuppliersTv.text=activeRiderList.size.toString()
                            binding.pendingApprovalTv.text=inActiveRiderList.size.toString()
                            binding.totalProductsTv.text=riderList.size.toString()
                        }
                    }
                    else{
                        binding.rvRiders.visibility=GONE
                    }
                }

                Status.ERROR -> {
                     AppUtil.stopLoader()


                }
            }
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
                            binding.tvCashRecieved.text = data.today_total_cash_received.toString()
                            binding.tvUnitSold.text = data.today_total_eggs_sold.toString()
                            binding.tvUnitDelieved.text = data.today_total_eggs_sold.toString()
                            binding.tvEggReturned.text = data.today_total_eggs_returned.toString()
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
}
