package com.example.unitedpoultry.exchange_return

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity

import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.databinding.ActivityExchangeOrReturnBinding
import com.example.unitedpoultry.exchange_return.adapter.ExchangeProductAdapter
import com.example.unitedpoultry.exchange_return.model.ReturnExchangeItem
import com.example.unitedpoultry.exchange_return.model.ReturnOrExchangeRequest
import com.example.unitedpoultry.exchange_return.viewmodel.RiderReturnOrExchangeSaleViewModel

import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExchangeOrReturnActivity : BaseActivity() {

    private lateinit var binding: ActivityExchangeOrReturnBinding
    private val viewModel: GetRiderProductViewModel by viewModel()
    private val viewModel1: RiderReturnOrExchangeSaleViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private val actionMap = mutableMapOf<Int, String>()
    private var productList = listOf<Product>()

    private var shopId: Int = 0
    private var areaId: Int = 0
    private var name: String = ""
    private var address: String = ""

    private var totalQuantity: String = ""
    private var totalAmount: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeOrReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = true, colorResId = android.R.color.white)

        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)
        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address

        setupRecycler()
        setupClicks()
        loadProducts()
    }

    private fun setupRecycler() {
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter =
            ExchangeProductAdapter(productList, quantityMap, actionMap) {
                // Optional: update subtotal/validation
            }
    }



    private fun setupClicks() {
        binding.btnCancel.setOnClickListener { finish() }

        binding.btnConfirm.setOnClickListener {
            val selectedItems = quantityMap.filter { it.value > 0 }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if all selected items have action
            for (id in selectedItems.keys) {
                if (actionMap[id].isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Please select Return or Exchange for all selected items",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            // Build list for API
            val itemsForApi = selectedItems.map { (id, qty) ->
                ReturnExchangeItem(
                    product_id = id,
                    qty = qty,
                    return_reason = actionMap[id]!!
                )
            }

            // Calculate totals to pass to next activity
            totalQuantity = itemsForApi.sumOf { it.qty }.toString()      // String
            totalAmount = itemsForApi.sumOf { item ->
                val product = productList.find { it.product_id == item.product_id }
                (product?.price?.toDoubleOrNull() ?: 0.0) * item.qty
            }.toString()                                                // String



            val request = ReturnOrExchangeRequest(
                shop_id = shopId,
                area_id = areaId,
                items = itemsForApi
            )

            callReturnExchangeApi(request)
        }
    }




    private fun loadProducts() {
        viewModel.getRiderProducts().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<RiderProductData>?
                        if (baseResponse?.result == "success" && baseResponse.data != null) {
                            productList = baseResponse.data.picked_items
                            binding.recyclerProducts.adapter =
                                ExchangeProductAdapter(productList, quantityMap, actionMap) {}
                        }
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun callReturnExchangeApi(request: ReturnOrExchangeRequest) {

        viewModel1.ReturnOrExchangeSale(request).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful && res.body()?.result == "success") {

                        Toast.makeText(this, "return sale successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, ReturnSucessActivity::class.java)
                        intent.putExtra("TOTAL_QUANTITY", totalQuantity)
                        intent.putExtra("TOTAL_AMOUNT", totalAmount)
                        intent.putExtra("SHOP_NAME", name)
                        intent.putExtra("ADDRESS", address)
                        intent.putExtra("INITIALS", getInitials(name))

                        intent.putExtra("SHOP_ID", shopId)
                        intent.putExtra("AREA_ID", areaId)

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to create sale", Toast.LENGTH_SHORT).show()
                    }
                }


                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
    }
}
