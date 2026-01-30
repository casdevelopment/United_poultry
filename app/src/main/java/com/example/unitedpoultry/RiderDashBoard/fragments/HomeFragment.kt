package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.viewmodel.DailyPaymentStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.DailyStatsViewModel
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.NewSale.Adapter.RiderProductHorizontalAdapter
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.rider_home.model.ReturnWasteRequestModel
import com.example.unitedpoultry.rider_home.viewmodel.ReturnWasteViewModel
import com.example.unitedpoultry.waste_return.RiderProductReturnActivity
import com.example.unitedpoultry.waste_return.RiderWasteProductActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var shopAdapter: RiderHomeAdapter

    private val ViewModel: DailyStatsViewModel by viewModel()
    private val ViewModel1: DailyPaymentStatsViewModel by viewModel()
    private val ViewModel2: ReturnWasteViewModel by viewModel()
    private val ViewModel3: GetRiderProductViewModel by viewModel()

    private var productList = listOf<Product>()

    private lateinit var productAdapter: RiderProductHorizontalAdapter



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
       setupRecycler()


    }

    private fun showData(){

        binding.tvTitle.text = "Hello, ${userData?.name ?: "User Name"}"


        binding.tvDescription.text = "${getTodayDay()}, ${getTodayDate()}"

    }



    private fun onclick(){

        binding.addEggsLayout.setOnClickListener{

            val intent = Intent(requireContext(), EggPickupActivity::class.java)
            startActivity(intent)
        }

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.openReturnDialog.setOnClickListener {

            showReturnWasteDialog()
        }

        binding.areaShortCut.setOnClickListener {
            // Navigate to AreaFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddressFragment()) // Replace with your AreaFragment
                .addToBackStack("AreaFragment") // Add to back stack so back button works
                .commit()
        }

    }

    override fun onResume() {
        super.onResume()
        loadDailyStats()
        loadDailyPaymentStats()
        loadProducts()
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
                           // binding.totalPicked.text = data.total_picked.toString()
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


    private fun loadDailyPaymentStats() {

        ViewModel1.getDailyPaymentStats().observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(requireActivity())

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<DailyPaymentStatsData>?
                        baseResponse?.data?.let { data ->
                           // binding.tvRecieved.text = "Rs ${data.today_total_cash_received}"

//                            binding.tvTotalAmount.text = "Rs ${data.today_total_cash_received}"
//                            binding.tvTotalSales.text = data.today_total_eggs_sold.toString()

                            // Cash received (String → Double → formatted)
                            val totalAmount = data.today_total_cash_received.toDoubleOrNull() ?: 0.0
                            binding.tvTotalAmount.text = "Rs ${formatNumber(totalAmount)}"

                            binding.tvTotalSales.text = formatNumber(data.today_total_eggs_sold)


                            // binding.totalWaste.text = data.todayTotalEggsWaste.toString()
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

    private fun showReturnWasteDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_egg_return, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnReturn = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReturn)
        val btnWaste = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnWaste)

        // 👉 OPEN RETURN ACTIVITY
        btnReturn.setOnClickListener {
            val intent = Intent(requireContext(), RiderProductReturnActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }


        btnWaste.setOnClickListener {
            val intent = Intent(requireContext(), RiderWasteProductActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }




    private fun hitReturnWasteApi(returnCount: Int, wasteCount: Int) {

        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        // Show loader if needed
        AppUtil.startLoader(requireActivity())

        val request = ReturnWasteRequestModel(
            returned = returnCount,
            waste = wasteCount,
            date = todayDate.toString().trim(),
        )

        // Example: ViewModel function to hit API
        ViewModel2.submitReturnWaste(request).observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(requireActivity())
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    Toast.makeText(requireContext(), "Submitted successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, refresh your daily stats

                    loadDailyStats()
                    loadDailyPaymentStats()
                    loadProducts()
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(requireContext(), response.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecycler() {
        productAdapter = RiderProductHorizontalAdapter()
        binding.recyclerProducts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerProducts.adapter = productAdapter
    }


    private fun loadProducts() {
        ViewModel3.getRiderProducts().observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.LOADING -> { /* show loader if needed */ }
                Status.SUCCESS -> {
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<RiderProductData>?
                        if (baseResponse?.result == "success" && baseResponse.data != null) {


                            val products = baseResponse.data.picked_items

                            productAdapter.submitList(products)

                            val totalEggsSum = products.sumOf { it.total_eggs }

                            val total = formatNumber(totalEggsSum)

                            binding.tvTotalEggs.text = "Eggs  ${total}"


                        } else {
                            Toast.makeText(
                                requireContext(),
                                baseResponse?.message ?: "Failed to fetch products",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        response.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
