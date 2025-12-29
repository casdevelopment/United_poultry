package com.example.unitedpoultry.AdminShopModule

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminShopDetailsBinding


class AdminShopDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminShopDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminShopDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )


        val name = intent.getStringExtra("name") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val discount = intent.getStringExtra("Discount") ?: ""
        val status = intent.getStringExtra("status") ?: ""

        val total = intent.getIntExtra("total", 0)
        val receivable = intent.getIntExtra("recieveable", 0)

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvStatus.text = status
        binding.totalOrders.text = total.toString()
        binding.tvDiscount.text = discount
        binding.tvReceivable.text = "Rs. $receivable"
        binding.tvAddress.text = address
        binding.tvDiscountPercent.text = "$discount on all orders"


        if (status.equals("Active", ignoreCase = true)) {
            binding.statusDot.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.green)
                )
        } else {
            binding.statusDot.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.black17)
                )
        }

        binding.btnEdit.setOnClickListener {

            val intent = Intent(this, AdminEditShopActivity::class.java)

            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("Discount", discount)
            intent.putExtra("status", status)
            intent.putExtra("total", total)
            intent.putExtra("recieveable", receivable)

            startActivity(intent)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        // Optional: handle save button click
//        binding.btnSave.setOnClickListener {
//            // TODO: Save updated data
//        }
    }
}
