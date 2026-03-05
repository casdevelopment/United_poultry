package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.model.SaleItem
import com.example.unitedpoultry.NewSale.model.SaleResponseData
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivitySaleSuccessBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class SaleSuccessActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleSuccessBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        val jsonData = intent.getStringExtra("sale_data_json")
        val saleData = Gson().fromJson(jsonData, SaleResponseData::class.java)

       binding.tvSubTitle.text = "Receipt #${saleData.id} has been generated"

        binding.tvShopName.text = saleData.shop_name
        binding.tvShopAddress.text = saleData.area_name
        binding.tvInitials.text = getInitials(saleData.shop_name)

        binding.tvPaymentType.text = saleData.payment_type


        binding.tvDiscount.text = "Rs ${
            if (saleData.discount % 1.0 == 0.0) {
                "%.0f".format(saleData.discount) // whole number → no decimal
            } else {
                "%.2f".format(saleData.discount) // decimal → 2 places
            }
        }"

        binding.tvDate.text = formatDateTime(saleData.sale_date)


        binding.tvTotal.text = "Rs ${
            if (saleData.total % 1.0 == 0.0) {
                "%.0f".format(saleData.total) // whole number → no decimal
            } else {
                "%.2f".format(saleData.total) // decimal → 2 places
            }
        }"


        val damageEggs = saleData.damage_eggs

        binding.tvExpire.text = "Peti: ${damageEggs.expire.peti} " + "Tray: ${damageEggs.expire.tray} " + "Single: ${damageEggs.expire.single}"

        binding.tvReturn.text = "Peti: ${damageEggs.return_.peti} " + "Tray: ${damageEggs.return_.tray} " + "Single: ${damageEggs.return_.single}"

        binding.tvLiquid.text = "Peti: ${damageEggs.liquid.peti} " + "Tray: ${damageEggs.liquid.tray} " + "Single: ${damageEggs.liquid.single}"



        val items: List<SaleItem>

        val itemsText = saleData.items.joinToString(separator = "\n") { item ->
            "${item.product_name}: ${item.qty}"
        }

        binding.tvQuantity.text = itemsText







        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            startActivity(intent)

        }


    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
    }

    fun formatDateTime(dateTimeString: String): String {
        return try {
            // Input format from server
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)

            // Desired output format
            val outputFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateTimeString
        } catch (e: Exception) {
            dateTimeString // fallback if parsing fails
        }
    }
}
