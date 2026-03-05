package com.example.unitedpoultry.rider_expense

import android.content.Intent
import android.os.Bundle
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
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

        binding.tvSubTitle.text = "Receipt # ${expenseData.id} has been generated"

        binding.tvExpenseTitle.text = expenseData.title
        binding.tvPaymentType.text = expenseData.payment_type
        binding.tvTotalAmount.text = "Rs ${expenseData.amount}"



        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            startActivity(intent)

        }


    }

}
