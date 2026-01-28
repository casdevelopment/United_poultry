package com.example.unitedpoultry.exchange_return


import android.content.Intent
import android.os.Bundle
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivityReturnSucessBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReturnSucessActivity : BaseActivity() {

    private lateinit var binding: ActivityReturnSucessBinding

    private var totalQuantity = 0
    private var totalAmount = 0.0

    private var discount = 0.0
    private var shopId = 0
    private var areaId = 0

    private var name: String = ""
    private var address: String = ""
    private var initials: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnSucessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        showData()


        binding.btnNewSale.setOnClickListener {
            finish()
        }



        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



    }

    private fun showData() {
        // Get values from Intent safely
        val totalQuantityStr = intent.getStringExtra("TOTAL_QUANTITY") ?: "0"
        val totalAmountStr = intent.getStringExtra("TOTAL_AMOUNT") ?: "0.0"
        name = intent.getStringExtra("SHOP_NAME") ?: ""
        address = intent.getStringExtra("ADDRESS") ?: ""
        initials = intent.getStringExtra("INITIALS") ?: ""

        // Convert strings to numeric types for formatting
        val totalQuantityInt = totalQuantityStr.toIntOrNull() ?: 0
        val totalAmountDouble = totalAmountStr.toDoubleOrNull() ?: 0.0

        // Assign to UI
        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = initials
        binding.tvQuantity.text = totalQuantityInt.toString()
        binding.tvTotal.text = "Rs. %.2f".format(totalAmountDouble)
        binding.tvDate.text = getTodayDate()
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

}
