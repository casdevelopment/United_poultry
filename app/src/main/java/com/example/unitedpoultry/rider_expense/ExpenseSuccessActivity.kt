package com.example.unitedpoultry.rider_expense

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.RotateTransformation
import com.example.unitedpoultry.databinding.ActivityExpenseSuccessBinding
import com.google.gson.Gson


class ExpenseSuccessActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseSuccessBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        val jsonData = intent.getStringExtra("expense_data_json")
        val expenseData = Gson().fromJson(jsonData, ExpenseModel::class.java)

        binding.tvSubTitle.text = "Receipt #${expenseData.id} has been generated"

        binding.tvExpenseTitle.text = expenseData.title
        binding.tvPaymentType.text = expenseData.payment_type
        binding.tvTotalAmount.text = "Rs ${expenseData.amount}"

        val imageUrl = expenseData.payment_record_url
        if (!imageUrl.isNullOrEmpty()) {
            binding.slipLayout.visibility = View.VISIBLE

            Glide.with(this)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .fitCenter() // keep aspect ratio
                        .transform(RotateTransformation(90f)) // rotate 90 degrees
                )
                .placeholder(binding.ivPaymentSlip.drawable)
                .into(binding.ivPaymentSlip)

        }

        if (expenseData.note != null) {
            binding.noteLayout.visibility = View.VISIBLE
            binding.etNote1.text = expenseData.note

        }




        binding.moveToDashBoard.setOnClickListener {
            finishActivity()
        }


    }

    private fun finishActivity() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishActivity()
    }

}
