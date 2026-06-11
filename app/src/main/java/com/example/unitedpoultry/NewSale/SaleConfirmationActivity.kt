package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.viewmodel.RiderNewSaleViewModel
import com.example.unitedpoultry.databinding.ActivitySaleConfirmationBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SaleConfirmationActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleConfirmationBinding
//    private val saleViewModel: RiderNewSaleViewModel by viewModel()
//
//    // Store intent data
//    private var shopId = 0
//    private var areaId = 0
//    private var subTotal = 0.0
//    private var discount = 0.0
//    private var total = 0.0
//    private var totalQuantity: Int = 0
//    private var name: String = ""
//    private var address: String = ""
//    private var initials: String = ""
//
//    private val items = mutableListOf<SaleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureStatusBar(isLightBackground = true, colorResId = android.R.color.white)

//        getIntentData()
//        setUiData()
//        setupClicks()
    }

//    // ---------------------------
//    // Get data from previous activity
//    // ---------------------------
//    private fun getIntentData() {
//        shopId = intent.getIntExtra("SHOP_ID", 0)
//        areaId = intent.getIntExtra("AREA_ID", 0)
//        subTotal = intent.getDoubleExtra("SUB_TOTAL", 0.0)
//        discount = intent.getDoubleExtra("DISCOUNT", 0.0)
//        total = intent.getDoubleExtra("TOTAL", 0.0)
//        totalQuantity = intent.getIntExtra("TOTAL_QUANTITY", 0)
//
//
//        name = intent.getStringExtra("SHOP_NAME") ?: ""
//        address = intent.getStringExtra("ADDRESS") ?: ""
//        initials = intent.getStringExtra("INITIALS") ?: ""
//
//        val productIds = intent.getIntegerArrayListExtra("PRODUCT_IDS") ?: arrayListOf()
//        val quantities = intent.getIntegerArrayListExtra("QUANTITIES") ?: arrayListOf()
//
//        for (i in productIds.indices) {
//            items.add(
//                SaleItem(
//                    product_id = productIds[i],
//                    qty = quantities[i]
//                )
//            )
//        }
//    }
//
//    // ---------------------------
//    // Set UI values
//    // ---------------------------
//    private fun setUiData() {
//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvSubtotal.text = "Rs. %.2f".format(subTotal)
//        binding.tvDiscount.text = "-Rs. %.2f".format(discount)
//        binding.tvTotal.text = "Rs. %.2f".format(total)
//        binding.tvTotalQuantity.text = totalQuantity.toString()
//
//        binding.tvInitials.text = initials
//
//    }
//
//    // ---------------------------
//    // Button clicks
//    // ---------------------------
//    private fun setupClicks() {
//        binding.btnSubmitSale.setOnClickListener {
//          //  callCreateSaleApi()
//        }
//
//        binding.btnEdit.setOnClickListener {
//            finish()
//        }
//    }

    // ---------------------------
    // API Call function
    // ---------------------------
//    private fun callCreateSaleApi() {
//
//        val request = SaleRequest(
//            shop_id = shopId,
//            area_id = areaId,
//            sub_total = subTotal,
//            discount = discount,
//            total = total,
//            cash_received = total,
//            items = items
//        )
//
//        saleViewModel.createNewSale(request).observe(this) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(this)
//
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val res = response.data
//                    if (res != null && res.isSuccessful && res.body()?.result == "success") {
//
//                        Toast.makeText(this, "Sale created successfully", Toast.LENGTH_SHORT).show()
//
//                        val intent = Intent(this, SaleSuccessActivity::class.java)
//                        intent.putExtra("TOTAL_QUANTITY", totalQuantity)
//                        intent.putExtra("TOTAL_AMOUNT", total)
//                        intent.putExtra("SHOP_NAME", name)
//                        intent.putExtra("ADDRESS", address)
//                        intent.putExtra("INITIALS", initials)
//
//                        intent.putExtra("SHOP_ID", shopId)
//                        intent.putExtra("AREA_ID", areaId)
//                        intent.putExtra("DISCOUNT", discount)
//
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        Toast.makeText(this, "Failed to create sale", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
}
