package com.example.unitedpoultry.rider_home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.databinding.ActivityEggPickupBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.adapter.EggPickUpAdapter
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import com.example.unitedpoultry.rider_home.model.PickedItem
import com.example.unitedpoultry.rider_home.model.PickedTodayProduct
import com.example.unitedpoultry.rider_home.viewmodel.EggPickupViewModel
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EggPickupActivity : BaseActivity() {

    private lateinit var binding: ActivityEggPickupBinding
    private val productViewModel: GetProductViewModel by viewModel()
    private val eggPickupViewModel: EggPickupViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private lateinit var productList2: List<PickedTodayProduct>
    var productList: MutableList<PickedTodayProduct> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEggPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productsJson = intent.getStringExtra("products")
        if (!productsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<PickedTodayProduct>>() {}.type
            productList2 = Gson().fromJson(productsJson, type)
        }

        configureStatusBar(true, android.R.color.white)
        loadProducts()
        setupRecycler()
        setupClicks()
    }

    private fun setupRecycler() {
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = EggPickUpAdapter(productList, quantityMap)
    }

    private fun loadProducts() {
        val pettiItem = PickedTodayProduct(
            id = -1,
            name = "Petti",
            eggs_count = 360,
            latest_price = 0.0,
            picked_quantity = 0,
            picked_qty = 0,
            total_eggs_picked = 0
        )

        productList.clear()
        productList.add(pettiItem)
        if (::productList2.isInitialized) {
            productList.addAll(productList2)
        }

        var pettiCount = 0
        for (item in productList) {
            if (item.name.contains("Tray", ignoreCase = true)) {
                pettiCount += item.picked_quantity / 12
                val remainingTrays = item.picked_quantity % 12
                item.picked_quantity = remainingTrays
            }
        }
        for (item in productList) {
            if (item.name.contains("Petti", ignoreCase = true)) {
                item.picked_quantity = pettiCount
            }
        }

        quantityMap.clear()
        for (item in productList) {
            if (item.picked_quantity > 0) {
                quantityMap[item.id] = item.picked_quantity
            }
        }

        binding.recyclerProducts.adapter = EggPickUpAdapter(productList, quantityMap)
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener

            var pettiQty = 0
            var trayQty = 0
            var trayProductId: Int? = null

            val items = mutableListOf<PickedItem>()

            for (product in productList) {
                val qty = quantityMap[product.id] ?: product.picked_quantity
                val name = product.name.orEmpty()
                Log.d("EggPickupActivity", "name: $name")
                Log.d("EggPickupActivity", "qty: $qty")
                Log.d("EggPickupActivity", "----------" )

                val isPetti = product.id == -1 || name.contains("Petti", ignoreCase = true)
                val isTray = name.contains("Tray", ignoreCase = true)

                when {
                    isPetti -> {
                        pettiQty = qty
                    }
                    isTray -> {
                        trayQty = qty
                        trayProductId = product.id
                    }
                    qty > 0 -> {
                        items.add(PickedItem(product_id = product.id, quantity = qty))
                    }
                }
            }

            val totalTrays = (pettiQty * 12) + trayQty

            if (totalTrays > 0) {
                val finalTrayId = trayProductId
                    ?: productList.find { it.name.contains("tray", ignoreCase = true) }?.id
                    ?: 2
                items.add(PickedItem(product_id = finalTrayId, quantity = totalTrays))
            }

            if (items.isEmpty()) {
                Toast.makeText(this, "Please enter quantity for at least one product", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = sdf.format(Date())

            val request = EggPickupRequest(
                date = todayDate,
                items = items
            )
            Log.d("EggPickupActivity", "Request: $request")

            savePickedEggs(request)
        }
    }

    private fun validateInputs(): Boolean {
        val hasQuantity = quantityMap.values.any { it > 0 } || productList.any { it.picked_quantity > 0 }

        if (!hasQuantity) {
            Toast.makeText(
                this,
                "Please enter quantity for at least one product",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun savePickedEggs(request: EggPickupRequest) {
        eggPickupViewModel.savePickedEggs(request).observe(this) { state ->
            when (state.status) {
                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val response = state.data ?: run {
                        Toast.makeText(this, "Empty response", Toast.LENGTH_LONG).show()
                        return@observe
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Toast.makeText(this, body.message, Toast.LENGTH_LONG).show()
                        if (body.result == "success") {
                            finish()
                        }
                    } else {
                        val errorMsg = try {
                            val errorJson = response.errorBody()?.string()
                            if (!errorJson.isNullOrEmpty()) {
                                Gson().fromJson(errorJson, BaseResponse::class.java)?.message
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                        Toast.makeText(this, errorMsg ?: "Server error", Toast.LENGTH_LONG).show()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}