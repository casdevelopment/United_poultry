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
import com.example.unitedpoultry.rider_home.model.ReturnWasteRequestModel
import com.example.unitedpoultry.rider_home.viewmodel.ReturnWasteViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var shopAdapter: RiderHomeAdapter

    private val ViewModel: DailyStatsViewModel by viewModel()
    private val ViewModel1: DailyPaymentStatsViewModel by viewModel()
    private val ViewModel2: ReturnWasteViewModel by viewModel()


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

    }

    override fun onResume() {
        super.onResume()
        loadDailyStats()
        loadDailyPaymentStats()
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
                            binding.tvRecieved.text = "Rs ${data.today_total_cash_received}"
                            binding.tvTotalAmount.text = "Rs ${data.today_total_cash_received}"
                            binding.tvTotalSales.text = data.today_total_eggs_sold.toString()
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
        // Inflate dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_egg_return, null)

        // Create AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Transparent background for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Find views
        val etReturnEggs = dialogView.findViewById<EditText>(R.id.etReturnEggs)
        val etWasteEggs = dialogView.findViewById<EditText>(R.id.etWasteEggs)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)

        val etReturnEggsError = dialogView.findViewById<TextView>(R.id.etReturnEggsError)
        val etWasteEggsError = dialogView.findViewById<TextView>(R.id.etWasteEggsError)

        // Hide errors initially
        etReturnEggsError.visibility = View.GONE
        etWasteEggsError.visibility = View.GONE

        // Cancel button
        btnCancel.setOnClickListener { dialog.dismiss() }

        // Save button
        btnSave.setOnClickListener {
            var valid = true

            // Hide errors before validating
            etReturnEggsError.visibility = View.GONE
            etWasteEggsError.visibility = View.GONE

            val returnEggs = etReturnEggs.text.toString().trim()
            val wasteEggs = etWasteEggs.text.toString().trim()

            // Validation for Return Eggs
            if (returnEggs.isEmpty()) {
                etReturnEggsError.visibility = View.VISIBLE
                etReturnEggsError.text = "Enter returned eggs"
                valid = false
            } else if (!returnEggs.matches(Regex("\\d+"))) { // only digits allowed
                etReturnEggsError.visibility = View.VISIBLE
                etReturnEggsError.text = "Enter valid number"
                valid = false
            }

            // Validation for Waste Eggs
            if (wasteEggs.isEmpty()) {
                etWasteEggsError.visibility = View.VISIBLE
                etWasteEggsError.text = "Enter waste eggs"
                valid = false
            } else if (!wasteEggs.matches(Regex("\\d+"))) { // only digits allowed
                etWasteEggsError.visibility = View.VISIBLE
                etWasteEggsError.text = "Enter valid number"
                valid = false
            }

            // Stop if any field is invalid
            if (!valid) return@setOnClickListener

            // All good, call your API here
             hitReturnWasteApi(returnEggs.toInt(), wasteEggs.toInt())

            dialog.dismiss()
        }

        // Clear error while typing
        etReturnEggs.addTextChangedListener { etReturnEggsError.visibility = View.GONE }
        etWasteEggs.addTextChangedListener { etWasteEggsError.visibility = View.GONE }

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
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(requireContext(), response.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
