package com.example.unitedpoultry.rider_expense

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.History.FullScreenImageActivity
import com.example.unitedpoultry.databinding.ActivityExpenseSuccessBinding
import com.example.unitedpoultry.rider_expense.Model.ExpenseModel
import com.example.unitedpoultry.rider_expense.adapter.ExpenseReceiptAdapter
import com.google.gson.Gson

class ExpenseSuccessActivity : BaseActivity() {

    private lateinit var binding: ActivityExpenseSuccessBinding
    private var fullImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        binding.ivPaymentSlip.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", fullImageUrl)
            startActivity(intent)
        }

        val jsonData = intent.getStringExtra("expense_data_json")
        val expenseData = Gson().fromJson(jsonData, ExpenseModel::class.java)

        binding.tvSubTitle.text = "Receipt generated successfully"
        binding.tvPaymentType.text = expenseData.paymentType?.replaceFirstChar { it.uppercase() }
        binding.tvTotalAmount.text = "Rs ${expenseData.totalAmount ?: 0}"

        // Display Itemized Expenses List
        val expenseItems = expenseData.expenses ?: emptyList()
        if (expenseItems.isNotEmpty()) {
            binding.recyclerReceiptExpenses.visibility = View.VISIBLE
            binding.recyclerReceiptExpenses.layoutManager = LinearLayoutManager(this)
            binding.recyclerReceiptExpenses.adapter = ExpenseReceiptAdapter(expenseItems)
        } else {
            binding.recyclerReceiptExpenses.visibility = View.GONE
        }

        fullImageUrl = expenseData.paymentRecordUrl
        if (!fullImageUrl.isNullOrEmpty()) {
            binding.slipLayout.visibility = View.VISIBLE
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(binding.ivPaymentSlip.drawable)
                .into(binding.ivPaymentSlip)
        }

        if (!expenseData.note.isNullOrEmpty()) {
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
}