package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderDetailsViewModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Notification.NotificationActivity
import com.example.unitedpoultry.rider_home.EggPickupActivity
import com.example.unitedpoultry.rider_home.adapter.RiderHomeAdapter
import com.example.unitedpoultry.databinding.FragmentHomeBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.viewmodel.DailyPaymentStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.DailyStatsViewModel
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.rider_expense.AddExpenseActivity
import com.example.unitedpoultry.rider_home.adapter.RiderHomeStatsAdapter
import com.example.unitedpoultry.rider_home.model.PickedToday
import com.example.unitedpoultry.rider_home.model.PickedTodayProduct
import com.example.unitedpoultry.rider_home.viewmodel.ReturnWasteViewModel
import com.example.unitedpoultry.status_check.UserStatusChecker
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppConstants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val ViewModel3: GetRiderProductViewModel by viewModel()
    private val ViewModel4: UserStatusViewModel by viewModel()

    private val riderViewModel: RiderDetailsViewModel by viewModel()
    private val sessionManager: SessionManager by inject()


    private lateinit var totalPickedAdapter: RiderHomeStatsAdapter
    private lateinit var remainingAdapter: RiderHomeStatsAdapter

    private lateinit var sabutAdapter: RiderHomeStatsAdapter
    private lateinit var melaAdapter: RiderHomeStatsAdapter
    private lateinit var tootaAdapter: RiderHomeStatsAdapter


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = 0
        }

        showData()
        onclick()
        setupRecyclerViews()


    }


    private fun showData(){

        binding.tvTitle.text = "Hello, ${userData?.name ?: "User Name"}"


        binding.tvDescription.text = "${getTodayDay()}, ${getTodayDate()}"

    }

    private fun refreshUserSession() {

        val riderId = userData?.id ?: return

        riderViewModel.getRiderDetails(riderId).observe(viewLifecycleOwner) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> AppUtil.startLoader(requireContext())

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data
                    if (response != null && response.isSuccessful) {

                        response.body()?.data?.let { riderModel ->

                            val updatedUser = mapRiderToLoginResponse(riderModel)


                            AppConstants.userData = updatedUser


                            sessionManager.userInfo(Gson().toJson(updatedUser))

                            showData()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                   // Toast.makeText(requireContext(),"Failed to refresh profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mapRiderToLoginResponse(rider: RiderModel): LoginResponseModel {
        return LoginResponseModel(
            id = rider.id,
            name = rider.name,
            email = rider.email ?: "",
            email_verified_at = rider.email_verified_at,
            phone_number = rider.phone_number ?: "",
            username = rider.username ?: "",
            address = rider.address ?: "",
            cnic = rider.cnic,
            image = rider.image,
            is_active = rider.is_active,
            role_id = rider.role_id,
            created_at = rider.created_at ?: "",
            updated_at = rider.updated_at ?: ""
        )
    }


    private fun onclick(){

        binding.addEggsLayout.setOnClickListener {

            UserStatusChecker.check(
                lifecycleOwner = viewLifecycleOwner,
                viewModel = ViewModel4,

                onActive = {
                    val intent = Intent(requireContext(), EggPickupActivity::class.java)
                    startActivity(intent)
                },

                onInactive = {
                    Toast.makeText(requireContext(), "Your account is inactive. Contact admin.", Toast.LENGTH_LONG).show()
                },

                onError = { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            )
        }


        binding.addExpenseLayout.setOnClickListener{

            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            startActivity(intent)
        }

//        binding.notifications.setOnClickListener {
//            val intent = Intent(requireContext(), NotificationActivity::class.java)
//            startActivity(intent)
//        }

//        binding.openReturnDialog.setOnClickListener {
//
//
//            UserStatusChecker.check(
//                lifecycleOwner = viewLifecycleOwner,
//                viewModel = ViewModel4,
//
//                onActive = {
//
//                    showReturnWasteDialog()
//                },
//
//                onInactive = {
//                    Toast.makeText(requireContext(), "Your account is inactive. Contact admin.", Toast.LENGTH_LONG).show()
//                },
//
//                onError = { message ->
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                }
//            )
//
//
//        }

        binding.newSaleLayout.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.nav_address
        }



    }

    override fun onResume() {
        super.onResume()
       // loadDailyStats()
      //  loadDailyPaymentStats()
        loadProducts()
        refreshUserSession()
    }

    private fun setupRecyclerViews() {

        totalPickedAdapter = RiderHomeStatsAdapter()

        binding.rvTotalPicked.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = totalPickedAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        remainingAdapter = RiderHomeStatsAdapter()

        binding.rvRemaining.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = remainingAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }


        sabutAdapter = RiderHomeStatsAdapter()

        binding.rvSabut.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = sabutAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }



        melaAdapter = RiderHomeStatsAdapter()

        binding.rvMela.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = melaAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }


        tootaAdapter = RiderHomeStatsAdapter()

        binding.rvToota.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = tootaAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }



    private fun loadProducts() {
        ViewModel3.getRiderProducts().observe(viewLifecycleOwner) { response ->
            when (response.status) {

                Status.LOADING -> {  }

                Status.SUCCESS -> {
                    val res = response.data
                    if (res != null && res.isSuccessful) {

                        val baseResponse = res.body() as BaseResponse<PickedToday>?
                        val data = baseResponse?.data

                        if (baseResponse?.result == "success" && data != null) {
                           // showProductEmptyState(false)
                            bindPickedItemsData(data)
                        }

                    }
//                    else showProductEmptyState(true)
                }

                Status.ERROR -> {
                    //showProductEmptyState(true)
                    Toast.makeText(requireContext(),"Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
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





    private fun bindPickedItemsData(data: PickedToday) {

        val rawTotalPickedList = data.total_picked.map { it.key to it.value }
        val rawRemainingList = data.remaining.map { it.key to it.value }
        val rawSabutList = data.categories.sabut.map { it.key to it.value }
        val rawMelaList = data.categories.mela.map { it.key to it.value }
        val rawTootaList = data.categories.mela.map { it.key to it.value }

        // Convert raw trays into Pettis + Remaining Trays for ALL lists
        val totalPickedList = convertTraysToPettiAndTrays(rawTotalPickedList)
        val remainingList = convertTraysToPettiAndTrays(rawRemainingList)
        val sabutList = convertTraysToPettiAndTrays(rawSabutList)
        val melaList = convertTraysToPettiAndTrays(rawMelaList)
        val tootaList = convertTraysToPettiAndTrays(rawTootaList)

        binding.tvLiquidQuantity.text = data.categories.liquid.kg.toString()

        totalPickedAdapter.submitList(totalPickedList)
        remainingAdapter.submitList(remainingList)
        sabutAdapter.submitList(sabutList)
        melaAdapter.submitList(melaList)
        tootaAdapter.submitList(tootaList)

        toggleSection(binding.rvTotalPicked, binding.emptyTotalPicked, totalPickedList.isNotEmpty())
        toggleSection(binding.rvRemaining, binding.emptyRemaining, remainingList.isNotEmpty())
        toggleSection(binding.rvSabut, binding.emptySabut, sabutList.isNotEmpty())
        toggleSection(binding.rvMela, binding.emptyMela, melaList.isNotEmpty())
        toggleSection(binding.rvToota, binding.emptyToota, tootaList.isNotEmpty())
    }

    private fun convertTraysToPettiAndTrays(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        var pettiCount = 0

        for ((key, count) in list) {
            if (count <= 0) continue // Skip 0 items

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

        // Insert Petti at the top if any exist (1 Petti = 12 Trays)
        if (pettiCount > 0) {
            result.add(0, "Petti" to pettiCount)
        }

        return result
    }


    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTodayDay(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
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


}
