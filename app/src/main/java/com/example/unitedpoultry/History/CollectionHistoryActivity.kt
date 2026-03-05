package com.example.unitedpoultry.History

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.unitedpoultry.R
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivitySaleHistoryBinding
import com.example.unitedpoultry.AdminHome.viewmodel.DailyPerformanceStatsViewModel
import com.example.unitedpoultry.History.model.CollectionHistory
import com.example.unitedpoultry.History.model.SaleHistory
import com.example.unitedpoultry.History.viewmodel.SaleHistoryDetailViewModel
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivityCollectionHistoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class CollectionHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionHistoryBinding

    private val viewModel: SaleHistoryDetailViewModel by viewModel()

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )



        // 🔹 Get ID from Intent
        id = intent.getIntExtra("ID", 0)

        onclick()


    }

    private fun onclick(){
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.backButton.setOnClickListener {
           finish()
        }

    }
    override fun onResume() {
        super.onResume()
        loadCollectionDetail()
    }


    private fun loadCollectionDetail() {

        viewModel.getCollectionDetail(id).observe(this) { response ->

                when (response.status) {

                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                    }

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()

                        val res = response.data
                        if (res != null && res.isSuccessful) {



                            // ⚡ Important: use SaleHistory, not SaleItem
                            val baseResponse = res.body() as BaseResponse<CollectionHistory>?

                            baseResponse?.data?.let { data ->

                                // Shop info
                                binding.tvShopName.text = data.shop_name
                                binding.tvShopAddress.text = data.shop_address
                                binding.tvInitials.text = getInitials(data.shop_name)



                                binding.tvPreviousBalance.text = "Rs ${data.previous_balance}"

                                binding.tvAmountCollected.text = "Rs ${data.amount_collected}"

                                binding.tvPaymentType.text = data.payment_type

                                binding.tvDate.text = formatDateTime(data.collected_at)

//                                // Totals
                                binding.tvRemainingBalance.text = "Rs ${data.remaining_balance}"

                            }
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        Toast.makeText(
                            this,
                            response.message ?: "Network error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
    }

    fun getInitials(name: String): String {
        return name
            .split(" ")                        // Split the name by spaces
            .filter { it.isNotBlank() }        // Ignore empty strings
            .map { it.first().uppercaseChar() } // Take the first char of each word, uppercase
            .joinToString("")                  // Combine into a single string
    }

    fun formatDateTime(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate // fallback if parsing fails
        }
    }
//
//    // 🔹 K & M formatter (textbox friendly)
//    private fun formatAmount(value: Double): String {
//        return when {
//            value >= 1_000_000 -> {
//                val v = value / 1_000_000
//                String.format("%.1f", v).removeSuffix(".0") + "M"
//            }
//            value >= 1_000 -> {
//                val v = value / 1_000
//                String.format("%.1f", v).removeSuffix(".0") + "K"
//            }
//            else -> value.toInt().toString()
//        }
//    }
}
