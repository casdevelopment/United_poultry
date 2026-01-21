package com.example.unitedpoultry.NewSale

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.widget.addTextChangedListener
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.NewSale.model.ShopModel
import com.example.unitedpoultry.adminproduct.adapter.ProductAdapter
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.databinding.ActivitySelectShopBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SaleFormActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleFormBinding
    private val productViewModel: GetProductViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<Product>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        setupRecycler()

        setupClicks()

    }

    private fun setupClicks(){

        binding.backArrow.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra("NAME") ?: "N/A"
        val address = intent.getStringExtra("ADDRESS")?: "N/A"
        val discount = intent.getStringExtra("DISCOUNT")?: "N/A"

        // 🔹 Set data to TextViews
        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)


        binding.btnConfirmSale.setOnClickListener {
            val intent = Intent(this, SaleConfirmationActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials",  getInitials(name))
            startActivity(intent)

        }

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


    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
