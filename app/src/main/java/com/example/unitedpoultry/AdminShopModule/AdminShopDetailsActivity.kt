package com.example.unitedpoultry.AdminShopModule

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.ShopDetailsViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminShopDetailsBinding
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminShopDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminShopDetailsBinding
    private val viewModel: ShopDetailsViewModel by viewModel()

    private var shopDetails: ShopDetailsResponseModel? = null
    private var areaId: Int = 0
    private var shopId: Int = 0

    // ✅ refresh flag
   // private var shouldRefresh = false

    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                when (result.data?.getStringExtra("ACTION")) {

                    "UPDATED" -> {
                        // Just refresh (onResume will handle it)
                    }

                    "DELETED" -> {
                        // Tell List to refresh & close Details
                        val intent = Intent()
                        intent.putExtra("ACTION", "DELETED")
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminShopDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(false, R.color.primary)

       // areaId = intent.getIntExtra("AREA_ID", 0)
        shopId = intent.getIntExtra("ID", 0)

        if (shopId == 0) {
            Toast.makeText(this, "Invalid shop id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //fetchShopDetails(shopId)

        binding.backArrow.setOnClickListener { finish() }

        binding.editShop.setOnClickListener {

            val shop = shopDetails
            if (shop == null) {
                Toast.makeText(this, "Shop data not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, AdminEditShopActivity::class.java)
            intent.putExtra("SHOP_ID", shop.id)
            intent.putExtra("SHOP_NAME", shop.name)
            intent.putExtra("SHOP_ADDRESS", shop.address)
            intent.putExtra("DISCOUNT", shop.discount_per_petti)
            intent.putExtra("CONTACT", shop.contact_person)
            intent.putExtra("PHONE", shop.phone_number)
            intent.putExtra("IS_ACTIVE", shop.is_active)
            intent.putExtra("AREA_ID", shop.area_id)
            intent.putExtra("IMAGE", shop.image)

            editLauncher.launch(intent)

        }
    }

    // ✅ refresh when coming back from edit
    override fun onResume() {
        super.onResume()
        //if (shouldRefresh) {
            fetchShopDetails(shopId)
            //shouldRefresh = false
       // }
    }

    private fun fetchShopDetails(shopId: Int) {

        viewModel.getShopDetails(shopId).observe(this) { apiResponse ->

            when (apiResponse.status) {

                com.example.unitedpoultry.network.Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                com.example.unitedpoultry.network.Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val retrofitResponse = apiResponse.data
                    if (retrofitResponse != null && retrofitResponse.isSuccessful) {

                        val baseResponse = retrofitResponse.body()
                        if (baseResponse?.result == "success") {
                            baseResponse.data?.let {
                                shopDetails = it
                                bindData(it)
                            }
                        }

                    } else {
                        showError(retrofitResponse)
                    }
                }

                com.example.unitedpoultry.network.Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindData(shop: ShopDetailsResponseModel) {

        binding.tvShopName.text = shop.name
        binding.tvShopAddress.text = shop.address
        binding.tvAddress.text = shop.address
        binding.tvDiscount.text = shop.discount_per_petti
        binding.tvDiscountPercent.text = "${shop.discount_per_petti}% on all orders"
        binding.tvContactName.text = shop.contact_person
        binding.tvPhoneNumber.text = shop.phone_number

        binding.totalOrders.text = "-"
        binding.tvReceivable.text = "-"
        binding.tvCreditLimit.text = "-"

        val statusText = if (shop.is_active) "Active" else "Inactive"
        binding.tvStatus.text = statusText

        val color = if (shop.is_active) R.color.green else R.color.black17
        binding.statusDot.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun showError(response: retrofit2.Response<*>?) {
        val message = try {
            val errorBody = response?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                Gson().fromJson(
                    errorBody,
                    com.example.unitedpoultry.network.retrofit.BaseResponse::class.java
                ).message
            } else "Something went wrong"
        } catch (e: Exception) {
            "Something went wrong"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
