package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.History.FullScreenImageActivity
import com.example.unitedpoultry.NewSale.model.SaleResponse
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivitySaleSuccessBinding
import com.example.unitedpoultry.util.AppConstants
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class SaleSuccessActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleSuccessBinding

    private var fullImageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        binding.ivPaymentSlip.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", fullImageUrl) // pass your image URL or local path
            startActivity(intent)
        }


        val jsonData = intent.getStringExtra("sale_data_json")
        val saleData = Gson().fromJson(jsonData, SaleResponse::class.java)

       binding.tvSubTitle.text = "Receipt #${saleData.id} has been generated"

        binding.tvShopName.text = saleData.shop_name
        binding.tvShopAddress.text = saleData.area_name
        binding.tvInitials.text = getInitials(saleData.shop_name)

        binding.tvPaymentType.text = saleData.payment_type


//        binding.tvDiscount.text = "Rs ${
//            if (saleData.discount % 1.0 == 0.0) {
//                "%.0f".format(saleData.discount) // whole number → no decimal
//            } else {
//                "%.2f".format(saleData.discount) // decimal → 2 places
//            }
//        }"

        binding.tvDate.text = formatDateTime(saleData.sale_date)


//        binding.tvTotal.text = "Rs ${
//            if (saleData.total % 1.0 == 0.0) {
//                "%.0f".format(saleData.total) // whole number → no decimal
//            } else {
//                "%.2f".format(saleData.total) // decimal → 2 places
//            }
//        }"


//        binding.tvBorrowedAmount.text = "Rs ${
//            if (saleData.borrowed_amount % 1.0 == 0.0) {
//                "%.0f".format(saleData.borrowed_amount) // whole number → no decimal
//            } else {
//                "%.2f".format(saleData.borrowed_amount) // decimal → 2 places
//            }
//        }"
//
//        binding.tvCollectionAmount.text = "Rs ${
//            if (saleData.collection_amount % 1.0 == 0.0) {
//                "%.0f".format(saleData.collection_amount) // whole number → no decimal
//            } else {
//                "%.2f".format(saleData.collection_amount) // decimal → 2 places
//            }
//        }"

        binding.tvDiscount.text = "Rs ${saleData.discount}"

        binding.tvTotal.text = "Rs ${saleData.total}"

        binding.tvBorrowedAmount.text = "Rs ${saleData.borrowed_amount}"

        binding.tvCollectionAmount.text = "Rs ${saleData.collection_amount}"


//        val damageEggs = saleData.damage_eggs
//
//        binding.tvExpire.text = "Peti: ${damageEggs.expire.peti} " + "Tray: ${damageEggs.expire.tray} " + "Single: ${damageEggs.expire.single}"
//
//        binding.tvReturn.text = "Peti: ${damageEggs.return_.peti} " + "Tray: ${damageEggs.return_.tray} " + "Single: ${damageEggs.return_.single}"
//
//        binding.tvLiquid.text = "Kg: ${damageEggs.liquid.kg}"



//        val itemsText = saleData.items.joinToString(separator = "\n") { item ->
//
//            val qty = item.qty
//            val peti = qty / 12
//            val tray = qty % 12
//
//            val quantityText = if (qty < 12) {
//                "Tray: $qty"
//            } else {
//                if (tray == 0) {
//                    "Peti: $peti"
//                } else {
//                    "Peti: $peti Tray: $tray"
//                }
//            }
//
//            "$quantityText"
//        }

     //   binding.tvQuantity.text = itemsText


        val imageUrl = saleData.payment_record_url
        if (!imageUrl.isNullOrEmpty()) {
            binding.slipLayout.visibility = View.VISIBLE

            fullImageUrl = AppConstants.ImageURL + imageUrl

            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(binding.ivPaymentSlip.drawable)
                .into(binding.ivPaymentSlip)

        }

        if (saleData.payment_note != null) {
            binding.noteLayout.visibility = View.VISIBLE
            binding.etNote1.text = saleData.payment_note

        }





        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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
