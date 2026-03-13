package com.example.unitedpoultry.rider_home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.adminproduct.adapter.ProductAdapter
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.databinding.ActivityEggPickupBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import com.example.unitedpoultry.rider_home.model.PickedItem
import com.example.unitedpoultry.rider_home.viewmodel.EggPickupViewModel
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EggPickupActivity : BaseActivity() {

    private lateinit var binding: ActivityEggPickupBinding
   // private val productViewModel: GetProductViewModel by viewModel()
    private val eggPickupViewModel: EggPickupViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEggPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)

      //  setupRecycler()
        setupClicks()
    }

//    override fun onResume() {
//        super.onResume()
//        loadProducts() // Reload products every time activity is resumed
//    }

//    private fun setupRecycler() {
//        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
//        binding.recyclerProducts.adapter = ProductAdapter(productList, quantityMap)
//    }

//    private fun loadProducts() {
//        productViewModel.getProducts().observe(this) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(this)
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val res = response.data
//                    if (res != null && res.isSuccessful) {
//                        val baseResponse = res.body() as BaseResponse<ProductData>?
//                        if (baseResponse?.result == "success" && baseResponse.data != null) {
//                            productList = baseResponse.data.products
//                            binding.recyclerProducts.adapter = ProductAdapter(productList, quantityMap)
//                        } else {
//                            Toast.makeText(this, baseResponse?.message ?: "Failed to fetch products", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun setupClicks() {

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {

            if (validateInputs()) {

                val trayQty = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
                val petiQty = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0

                val totalTrays = (petiQty * 12) + trayQty


                if (totalTrays <= 0) {
                    Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val items = listOf(
                    PickedItem(
                        product_id = 2,   // Tray ID
                        quantity = totalTrays
                    )
                )

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate = sdf.format(Date())

                val request = EggPickupRequest(
                    date = todayDate,
                    items = items
                )

                savePickedEggs(request)


            }
        }
    }



    private fun validateInputs(): Boolean {

        var valid = true

        binding.tvTrayQuantityError.visibility = View.GONE
        binding.tvPetiQuantityError.visibility = View.GONE

        val trayQty = binding.etTrayQuantity.text.toString().trim().toIntOrNull() ?: 0
        val petiQty = binding.etPetiQuantity.text.toString().trim().toIntOrNull() ?: 0

        // Check both empty
        if (trayQty == 0 && petiQty == 0) {
           // binding.tvTrayQuantityError.visibility = View.VISIBLE
           // binding.tvTrayQuantityError.text = "Enter Tray or Peti quantity"
            Toast.makeText(this,  "Enter Tray or Peti quantity", Toast.LENGTH_LONG).show()


            valid = false
        }

        // Tray limit validation
        if (trayQty > 11) {
            binding.tvTrayQuantityError.visibility = View.VISIBLE
            binding.tvTrayQuantityError.text = "Cannot exceed 11 trays"
            valid = false
        }

//        // Negative or zero check
//        if (trayQty < 0) {
//            binding.tvTrayError.visibility = View.VISIBLE
//            binding.tvTrayError.text = "Tray quantity must be greater than 0"
//            valid = false
//        }

//        if (petiQty < 0) {
//            binding.tvPetiError.visibility = View.VISIBLE
//            binding.tvPetiError.text = "Peti quantity must be greater than 0"
//            valid = false
//        }

        return valid
    }


//    private fun setupClicks() {
//        binding.backArrow.setOnClickListener { finish() }
//
//
//
//
//
//        binding.btnSave.setOnClickListener {
//
//            var petiQty = 0
//            var trayQty = 0
//
//            quantityMap.forEach { (productId, qty) ->
//
//                val product = productList.find { it.id == productId }
//
//                if (product?.name?.contains("peti", true) == true) {
//                    petiQty = qty
//                }
//                else if (product?.name?.contains("tray", true) == true) {
//                    trayQty = qty
//                }
//            }
//
//            // Convert Peti to trays
//            val totalTrays = (petiQty * 12) + trayQty
//
//            if (totalTrays <= 0) {
//                Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val items = listOf(
//                PickedItem(
//                    product_id = 2,   // Tray ID
//                    quantity = totalTrays
//                )
//            )
//
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val todayDate = sdf.format(Date())
//
//            val request = EggPickupRequest(
//                date = todayDate,
//                items = items
//            )
//
//            savePickedEggs(request)
//        }
//    }

    private fun savePickedEggs(request: EggPickupRequest) {
        eggPickupViewModel.savePickedEggs(request).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<Any>?
                        Toast.makeText(this, baseResponse?.message ?: "Saved successfully", Toast.LENGTH_LONG).show()
                        if (baseResponse?.result == "success")
                            finish()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTokenFromPrefs(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }
}
