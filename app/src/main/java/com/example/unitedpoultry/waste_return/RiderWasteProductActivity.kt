package com.example.unitedpoultry.waste_return

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.adminproduct.adapter.ProductAdapter
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.databinding.ActivityEggPickupBinding
import com.example.unitedpoultry.databinding.ActivityRiderWasteProductBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import com.example.unitedpoultry.rider_home.model.PickedItem
import com.example.unitedpoultry.rider_home.viewmodel.EggPickupViewModel
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.waste_return.model.RiderReturnItem
import com.example.unitedpoultry.waste_return.model.RiderReturnRequest
import com.example.unitedpoultry.waste_return.model.RiderWasteRequest
import com.example.unitedpoultry.waste_return.viewmodel.RiderWasteProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiderWasteProductActivity : BaseActivity() {

    private lateinit var binding: ActivityRiderWasteProductBinding
    private val productViewModel: GetProductViewModel by viewModel()
    private val ViewModel: RiderWasteProductViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiderWasteProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)

        setupRecycler()
        setupClicks()
    }

    override fun onResume() {
        super.onResume()
        loadProducts() // Reload products every time activity is resumed
    }

    private fun setupRecycler() {
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = ProductAdapter(productList, quantityMap)
    }

    private fun loadProducts() {
        productViewModel.getProducts().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<ProductData>?
                        if (baseResponse?.result == "success" && baseResponse.data != null) {
                            productList = baseResponse.data.products
                            binding.recyclerProducts.adapter = ProductAdapter(productList, quantityMap)
                        } else {
                            Toast.makeText(this, baseResponse?.message ?: "Failed to fetch products", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener { finish() }

        binding.btnDeleteShops.setOnClickListener {
            val items = quantityMap.filter { it.value > 0 }
                .map { RiderReturnItem(product_id = it.key, qty = it.value) }


            if (items.isEmpty()) {
                Toast.makeText(this, "Please enter quantity for at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = sdf.format(Date())

            val request = RiderWasteRequest(
                date = todayDate,
                waste_items = items
            )
            saveWasteProduct(request)
        }
    }

    private fun saveWasteProduct(request: RiderWasteRequest) {

        ViewModel.RiderWasteProduct(request).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<RiderProductData>?
                        Toast.makeText(this, baseResponse?.message ?: "Saved successfully", Toast.LENGTH_LONG).show()
                        if (baseResponse?.result == "success")
                            finish()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, response.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
