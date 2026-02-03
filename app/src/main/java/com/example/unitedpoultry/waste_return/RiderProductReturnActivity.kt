package com.example.unitedpoultry.waste_return

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity

import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.databinding.ActivityRiderProductReturnBinding

import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.waste_return.adapter.RiderReturnProductAdapter
import com.example.unitedpoultry.waste_return.model.RiderReturnItem
import com.example.unitedpoultry.waste_return.model.RiderReturnRequest
import com.example.unitedpoultry.waste_return.viewmodel.RiderReturnProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiderProductReturnActivity : BaseActivity() {

    private lateinit var binding: ActivityRiderProductReturnBinding
    private val viewModel: GetRiderProductViewModel by viewModel()
    private val viewModel1: RiderReturnProductViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<Product>()

//    private var shopId: Int = 0
//    private var areaId: Int = 0
//    private var name: String = ""
//    private var address: String = ""
//
//    private var totalQuantity: String = ""
//    private var totalAmount: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiderProductReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = true, colorResId = android.R.color.white)

        setupRecycler()
        setupClicks()
    }

    private fun setupRecycler() {
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = RiderReturnProductAdapter(productList, quantityMap) {
            }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }



    private fun setupClicks() {
        binding.btnCancel.setOnClickListener { finish() }

        binding.backArrow.setOnClickListener { finish() }

        binding.btnConfirm.setOnClickListener {
            val selectedItems = quantityMap.filter { it.value > 0 }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if all selected items have action
//            for (id in selectedItems.keys) {
//                if (actionMap[id].isNullOrEmpty()) {
//                    Toast.makeText(
//                        this,
//                        "Please select Return or Exchange for all selected items",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//            }

            val itemsForApi = selectedItems.map { (id, qty) ->
                RiderReturnItem(
                    product_id = id,
                    qty = qty,
                )
            }

            val todayDate: String = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())



            val request = RiderReturnRequest(
                date = todayDate,
                returned_items = itemsForApi
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
                                RiderReturnProductAdapter(productList, quantityMap) {}
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

    private fun callReturnExchangeApi(request: RiderReturnRequest) {

        viewModel1.riderReturnProduct(request).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful && res.body()?.result == "success") {

                        Toast.makeText(this, "return sale successfully", Toast.LENGTH_SHORT).show()

//                        val intent = Intent(this, RiderProductReturnActivity::class.java)
//                        intent.putExtra("TOTAL_QUANTITY", totalQuantity)
//                        intent.putExtra("TOTAL_AMOUNT", totalAmount)
//                        intent.putExtra("SHOP_NAME", name)
//                        intent.putExtra("ADDRESS", address)
//                        intent.putExtra("INITIALS", getInitials(name))
//
//                        intent.putExtra("SHOP_ID", shopId)
//                        intent.putExtra("AREA_ID", areaId)
//
//                        startActivity(intent)
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

}
