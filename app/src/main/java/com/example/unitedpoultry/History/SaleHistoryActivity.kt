package com.example.unitedpoultry.History

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.unitedpoultry.R
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivitySaleHistoryBinding
import com.example.unitedpoultry.AdminHome.viewmodel.DailyPerformanceStatsViewModel
import com.example.unitedpoultry.History.model.SaleHistoryData
import com.example.unitedpoultry.History.viewmodel.SaleHistoryDetailViewModel
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SaleHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleHistoryBinding

    private val viewModel: SaleHistoryDetailViewModel by viewModel()

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        binding = ActivitySaleHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Get ID from Intent
        id = intent.getIntExtra("ID", 0)

        onclick()


    }

    private fun onclick(){
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
//    override fun onResume() {
//        super.onResume()
//        loadDailyPaymentStats()
//    }
//
//
//    private fun loadDailyPaymentStats() {
//
//        // 🔹 Pass sellerId to API
//        viewModel.getSaleDetail(id)
//            .observe(this) { response ->
//
//                when (response.status) {
//
//                    Status.LOADING -> {
//                        AppUtil.startLoader(this)
//                    }
//
//                    Status.SUCCESS -> {
//                        AppUtil.stopLoader()
//
//                        val res = response.data
//                        if (res != null && res.isSuccessful) {
//
//                            val baseResponse =
//                                res.body() as BaseResponse<SaleItem>?
//
//                            baseResponse?.data?.let { data ->
//
//                                binding.tvShopName.text = data.shop_name
//                                binding.tvShopAddress.text = "Area ${data.area_name}"
//
//                                binding.tvDate.text = data.sale_date
//
//                                val subTotal = data.sub_total.toDoubleOrNull() ?: 0.0
//                                val discount = data.discount.toDoubleOrNull() ?: 0.0
//                                val total = data.total.toDoubleOrNull() ?: 0.0
//
//                                binding.tvSubTotal.text = "Rs ${formatAmount(subTotal)}"
//                                binding.tvDiscount.text = "-Rs ${formatAmount(discount)}"
//                                binding.tvTotal.text = "Rs ${formatAmount(total)}"
//                                binding.tvAmount.text = "Rs ${formatAmount(total)}"
//
//                            }
//                        }
//                    }
//
//                    Status.ERROR -> {
//                        AppUtil.stopLoader()
//                        Toast.makeText(
//                            this,
//                            response.message ?: "Network error",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//    }
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
