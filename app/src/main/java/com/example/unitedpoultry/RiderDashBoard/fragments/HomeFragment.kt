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
  //  private lateinit var shopAdapter: RiderHomeAdapter

//    private val ViewModel: DailyStatsViewModel by viewModel()
//    private val ViewModel1: DailyPaymentStatsViewModel by viewModel()
//    private val ViewModel2: ReturnWasteViewModel by viewModel()
    private val ViewModel3: GetRiderProductViewModel by viewModel()
    private val ViewModel4: UserStatusViewModel by viewModel()

//    private var productList = listOf<Product>()
//
//    private lateinit var productAdapter: RiderProductHorizontalAdapter

    private val riderViewModel: RiderDetailsViewModel by viewModel()
    private val sessionManager: SessionManager by inject()


    private lateinit var totalPickedAdapter: RiderHomeStatsAdapter
    private lateinit var remainingAdapter: RiderHomeStatsAdapter
    private lateinit var expireAdapter: RiderHomeStatsAdapter
    private lateinit var returnAdapter: RiderHomeStatsAdapter


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

            // Call your reusable UserStatusChecker
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

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

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

//    private fun loadDailyStats() {
//
//        ViewModel.getDailyStats().observe(viewLifecycleOwner) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(requireActivity())
//
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val res = response.data
//                    if (res != null && res.isSuccessful) {
//                        val baseResponse = res.body() as BaseResponse<EggPickupData>?
//                        baseResponse?.data?.let { data ->
//                           // binding.totalPicked.text = data.total_picked.toString()
//                            binding.totalSold.text = data.total_sold.toString()
//                            binding.totalReturned.text = data.total_returned.toString()
//                            binding.totalWaste.text = data.total_waste.toString()
//                        }
//                    }
//                }
//
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(
//                        requireContext(),
//                        response.message ?: "Network error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }


//    private fun loadDailyPaymentStats() {
//
//        ViewModel1.getDailyPaymentStats().observe(viewLifecycleOwner) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(requireActivity())
//
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val res = response.data
//                    if (res != null && res.isSuccessful) {
//                        val baseResponse = res.body() as BaseResponse<DailyPaymentStatsData>?
//                        baseResponse?.data?.let { data ->
//                           // binding.tvRecieved.text = "Rs ${data.today_total_cash_received}"
//
////                            binding.tvTotalAmount.text = "Rs ${data.today_total_cash_received}"
////                            binding.tvTotalSales.text = data.today_total_eggs_sold.toString()
//
//                            // Cash received (String → Double → formatted)
//                            val totalAmount = data.today_total_cash_received.toDoubleOrNull() ?: 0.0
//                            binding.tvTotalAmount.text = "Rs ${formatNumber(totalAmount)}"
//
//                            binding.tvTotalSales.text = formatNumber(data.today_total_eggs_sold)
//
//
//                            // binding.totalWaste.text = data.todayTotalEggsWaste.toString()
//                        }
//                    }
//                }
//
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(
//                        requireContext(),
//                        response.message ?: "Network error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }

//    private fun showReturnWasteDialog() {
//
//        val dialogView = layoutInflater.inflate(R.layout.dialog_egg_return, null)
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .setCancelable(true)
//            .create()
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//        val btnReturn = dialogView.findViewById<MaterialButton>(R.id.btnReturn)
//        val btnWaste = dialogView.findViewById<MaterialButton>(R.id.btnWaste)
//
//        // 👉 OPEN RETURN ACTIVITY
//        btnReturn.setOnClickListener {
//            val intent = Intent(requireContext(), RiderProductReturnActivity::class.java)
//            startActivity(intent)
//            dialog.dismiss()
//        }
//
//
//        btnWaste.setOnClickListener {
//            val intent = Intent(requireContext(), RiderWasteProductActivity::class.java)
//            startActivity(intent)
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }




//    private fun hitReturnWasteApi(returnCount: Int, wasteCount: Int) {
//
//        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
//        // Show loader if needed
//        AppUtil.startLoader(requireActivity())
//
//        val request = ReturnWasteRequestModel(
//            returned = returnCount,
//            waste = wasteCount,
//            date = todayDate.toString().trim(),
//        )
//
//        // Example: ViewModel function to hit API
//        ViewModel2.submitReturnWaste(request).observe(viewLifecycleOwner) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(requireActivity())
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(requireContext(), "Submitted successfully", Toast.LENGTH_SHORT).show()
//                    // Optionally, refresh your daily stats
//
//                    loadDailyStats()
//                    loadDailyPaymentStats()
//                    loadProducts()
//                }
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(requireContext(), response.message ?: "Network error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

//    private fun setupRecycler() {
//        productAdapter = RiderProductHorizontalAdapter()
//        binding.recyclerProducts.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerProducts.adapter = productAdapter
//    }

//    private fun loadProducts() {
//        ViewModel3.getRiderProducts().observe(viewLifecycleOwner) { response ->
//            when (response.status) {
//                Status.LOADING -> { /* show loader if needed */ }
//
//                Status.SUCCESS -> {
//                    val res = response.data
//                    if (res != null && res.isSuccessful) {
//                        val baseResponse = res.body() as BaseResponse<PickedItemsResponse>?
//                        if (baseResponse?.result == "success" && baseResponse.data != null) {
//
//                            val products = baseResponse.data.picked_items
//
//                            if (products.isNullOrEmpty()) {
//                                showProductEmptyState(true)
//                            } else {
//                                showProductEmptyState(false)
//                                productAdapter.submitList(products)
//
//                                val totalEggsSum = products.sumOf { it.total_eggs }
//                               // val total = formatNumber(totalEggsSum)
//                                binding.tvTotalEggs.text = "Eggs  ${totalEggsSum}"
//                            }
//
//                        } else {
//                            showProductEmptyState(true)
//                            Toast.makeText(
//                                requireContext(),
//                                baseResponse?.message ?: "Failed to fetch products",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } else {
//                        showProductEmptyState(true)
//                    }
//                }
//
//                Status.ERROR -> {
//                    showProductEmptyState(true)
//                    Toast.makeText(
//                        requireContext(),
//                        response.message ?: "Network Error",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }

//    private fun showProductEmptyState(show: Boolean) {
//        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
//        binding.recyclerProducts.visibility = if (show) View.GONE else View.VISIBLE
//    }


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

        val totalPickedList = data.total_picked.map { it.key to it.value }
        val remainingList = data.remaining.map { it.key to it.value }
        val expireList = data.categories.expire.map { it.key to it.value }
        val returnList = data.categories.`return`.map { it.key to it.value }

        binding.tvLiquidQuantity.text = data.categories.liquid.kg.toString()

        totalPickedAdapter.submitList(totalPickedList)
        remainingAdapter.submitList(remainingList)
        expireAdapter.submitList(expireList)
        returnAdapter.submitList(returnList)

        toggleSection(binding.rvTotalPicked, binding.emptyTotalPicked, totalPickedList.isNotEmpty())
        toggleSection(binding.rvRemaining, binding.emptyRemaining, remainingList.isNotEmpty())
        toggleSection(binding.rvExpire, binding.emptyExpire, expireList.isNotEmpty())
        toggleSection(binding.rvReturn, binding.emptyReturn, returnList.isNotEmpty())
    }

//    private fun bindPickedItemsData(data: PickedItemsResponse) {
//
//        // DATE
//      //  binding.tvDate.text = data.date
//
//        // TOTAL PICKED
////        binding.tvTotalPickedPeti.text = "Peti: ${data.total_picked.peti}"
////        binding.tvTotalPickedTray.text = "Tray: ${data.total_picked.tray}"
//
//        val totalTrays = data.total_picked.tray ?: 0
//
//        val peti = totalTrays / 12
//        val tray = totalTrays % 12
//
//        binding.tvTotalPickedPeti.text = "Peti: $peti"
//        binding.tvTotalPickedTray.text = "Tray: $tray"
//
//        // REMAINING
////        binding.tvRemainingPeti.text = data.remaining.total_peti.toString()
////        binding.tvRemainingTray.text = data.remaining.total_trays.toString()
//
//        val remainingTrays = data.remaining.total_trays ?: 0
//
//        val rPeti = remainingTrays / 12
//        val rTray = remainingTrays % 12
//
//        binding.tvRemainingPeti.text = rPeti.toString()
//        binding.tvRemainingTray.text = rTray.toString()
//
//        // EXPIRE
//        binding.tvExpirePeti.text = "Peti: ${data.categories.expire.peti}"
//        binding.tvExpireTray.text = "Tray: ${data.categories.expire.tray}"
//        binding.tvExpireSingle.text = "Eggs: ${data.categories.expire.single}"
//
//        // RETURN
//        binding.tvReturnPeti.text = "Peti: ${data.categories.`return`.peti}"
//        binding.tvReturnTray.text = "Tray: ${data.categories.`return`.tray}"
//        binding.tvReturnSingle.text = "Eggs: ${data.categories.`return`.single}"
//
//        // LIQUID
//     //   binding.tvLiquidPeti.text = "Peti: ${data.categories.liquid.peti}"
//        binding.tvLiquid.text = "Kg: ${data.categories.liquid.kg}"
//     //   binding.tvLiquidSingle.text = "Eggs: ${data.categories.liquid.single}"
//    }

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
