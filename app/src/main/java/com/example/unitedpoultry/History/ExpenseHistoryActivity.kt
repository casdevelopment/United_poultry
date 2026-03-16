package com.example.unitedpoultry.History

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.unitedpoultry.R
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.History.model.ExpenseItem
import com.example.unitedpoultry.History.viewmodel.SaleHistoryDetailViewModel
import com.example.unitedpoultry.databinding.ActivityExpenseHistoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseHistoryBinding

    private val viewModel: SaleHistoryDetailViewModel by viewModel()

    private var id: Int = 0

    private var fullImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )


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

        binding.ivPaymentSlip.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", fullImageUrl)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        loadExpenseDetail()
    }


    private fun loadExpenseDetail() {

        viewModel.getExpenseDetail(id).observe(this) { response ->

            when (response.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val res = response.data
                    if (res != null && res.isSuccessful) {

                        val baseResponse = res.body() as BaseResponse<ExpenseItem>?

                        baseResponse?.data?.let { data ->

                            binding.tvExpenseTitle.text = data.title
                            binding.tvPaymentType.text = data.payment_type


                            binding.tvTotalAmount.text = "Rs ${data.amount}"

                            fullImageUrl = data.payment_record_url
                            if (!fullImageUrl.isNullOrEmpty()) {
                                binding.slipLayout.visibility = View.VISIBLE



                                Glide.with(this)
                                    .load(fullImageUrl)
                                    .placeholder(binding.ivPaymentSlip.drawable)
                                    .into(binding.ivPaymentSlip)

                            }

                            if (data.note != null) {
                                binding.noteLayout.visibility = View.VISIBLE
                                binding.etNote1.text = data.note

                            }


                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    fun formatDateTime(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate
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
