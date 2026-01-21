package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminHome.model.ShopvisitedModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Notification.NotificationActivity
import com.example.unitedpoultry.rider_home.EggPickupActivity
import com.example.unitedpoultry.rider_home.adapter.RiderHomeAdapter
import com.example.unitedpoultry.databinding.FragmentHomeBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.viewmodel.DailyStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.EggPickupViewModel
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var shopAdapter: RiderHomeAdapter

    private val ViewModel: DailyStatsViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)

        // Optional: Change status bar icons to dark if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = 0 // light icons: 0, dark icons: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        showData()
        onclick()

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        val historyList = listOf(
            ShopvisitedModel("Jalal Sons", "Last visit: 3 days ago. Rs. 12500. 12 orders", R.drawable.visitedshopimage1),
            ShopvisitedModel("Al-Fatah Store", "Last visit: 5 days ago. Cash collection", R.drawable.visitedshopimage2),
            ShopvisitedModel("Green Valley Mart", "Last visit: 2 days ago. Cash collection", R.drawable.visitedshopimage1),
            ShopvisitedModel("Mini Mart Central", "Last visit: 1 day ago. 9 Boxes. Credit", R.drawable.visitedshopimage2)
        )

        shopAdapter = RiderHomeAdapter(historyList.toMutableList())
        binding.rvVisitedShops.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisitedShops.adapter = shopAdapter

    }

    private fun showData(){

        binding.tvTitle.text = "Hello, ${userData?.name ?: "User Name"}"
//        binding.tvEmail.text = userData?.email ?: "Email"
//
//        binding.tvInitials.text = getInitials(userData?.name)

    }



    private fun onclick(){

        binding.addEggsLayout.setOnClickListener{

            val intent = Intent(requireContext(), EggPickupActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        loadDailyStats()
    }

    private fun loadDailyStats() {
        ViewModel.getDailyStats().observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(requireActivity())

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<EggPickupData>?
                        baseResponse?.data?.let { data ->
                            binding.totalPicked.text = data.total_picked.toString()
                            binding.totalSold.text = data.total_sold.toString()
                            binding.totalReturned.text = data.total_returned.toString()
                            binding.totalWaste.text = data.total_waste.toString()
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
