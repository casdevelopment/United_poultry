package com.example.unitedpoultry.AdminShopModule

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAddNewShopBinding
import com.example.unitedpoultry.databinding.ActivityAdminEditShopBinding
import com.example.unitedpoultry.databinding.ActivityAdminShopDetailsBinding


class AdminEditShopActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminEditShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        val name = intent.getStringExtra("name") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val discount = intent.getStringExtra("Discount") ?: ""
//        val status = intent.getStringExtra("status") ?: ""
//        val total = intent.getIntExtra("total", 0)
//        val receivable = intent.getIntExtra("recieveable", 0)

        // Set data to fields
        binding.etShopName.setText(name)
        binding.etAddress.setText(address)
        binding.etDiscount.setText(discount)



        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnUpdate.setOnClickListener {
            val intent = Intent(this, AdminDashBoardActivity::class.java)
            startActivity(intent)
        }

    }
}
