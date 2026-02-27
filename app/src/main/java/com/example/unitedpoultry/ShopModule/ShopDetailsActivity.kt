package com.example.unitedpoultry.ShopModule

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponse
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.CollectionformActivity
import com.example.unitedpoultry.NewSale.SaleFormActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.Adapter.RecentActivityAdapter
import com.example.unitedpoultry.ShopModule.model.RecentActivityModel
import com.example.unitedpoultry.ShopModule.viewmodel.RiderShopDetailsViewModel
import com.example.unitedpoultry.databinding.ActivityShopDetailsBinding
import com.example.unitedpoultry.status_check.UserStatusChecker
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShopDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityShopDetailsBinding
    private val viewModel: RiderShopDetailsViewModel by viewModel()
    private val viewModel1: UserStatusViewModel by viewModel()

    private var shopDetails: ShopDetailsResponse? = null
    private var shopId: Int = 0
    private var areaId: Int = 0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(false, R.color.primary)

        // areaId = intent.getIntExtra("AREA_ID", 0)
        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)

        if (shopId == 0) {
            Toast.makeText(this, "Invalid shop id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        binding.backArrow.setOnClickListener { finish() }

        binding.btnNewSale.setOnClickListener {
            // First check if the user is active
            UserStatusChecker.check(
                lifecycleOwner = this,
                viewModel = viewModel1,

                onActive = {
                    shopDetails?.let { shop ->
                        val intent = Intent(this, SaleFormActivity::class.java)
                        intent.putExtra("ID", shop.id)
                        intent.putExtra("AREA_ID", areaId)
                        intent.putExtra("NAME", shop.name)
                        intent.putExtra("ADDRESS", shop.address)
                        intent.putExtra("DISCOUNT", shop.discount_per_petti.toString())
                        startActivity(intent)
                    } ?: run {
                        Toast.makeText(this, "Shop details not loaded yet", Toast.LENGTH_SHORT).show()
                    }
                },

                onInactive = {
                    Toast.makeText(
                        this,
                        "Your account is inactive. Contact admin.",
                        Toast.LENGTH_LONG
                    ).show()
                },

                onError = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            )
        }


        binding.btnCollectPayment.setOnClickListener {
            shopDetails?.let { shop ->

                val intent = Intent(this, CollectionformActivity::class.java)

                intent.putExtra("SHOP_ID", shop.id)
                intent.putExtra("AREA_ID", areaId)
                intent.putExtra("NAME", shop.name)
                intent.putExtra("ADDRESS", shop.address)
                intent.putExtra("DISCOUNT", shop.discount_per_petti)
                startActivity(intent)

            } ?: run {
                Toast.makeText(this, "Shop details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerRecentActivities.layoutManager =
            LinearLayoutManager(this)


    }

    // ✅ refresh when coming back from edit
    override fun onResume() {
        super.onResume()

        fetchShopDetails(shopId)

    }

    private fun fetchShopDetails(shopId: Int) {

        viewModel.getRiderShopDetails(shopId).observe(this) { apiResponse ->

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

//    private fun bindData(shop: ShopDetailsResponseModel) {
//
//        binding.tvShopName.text = shop.name
//        binding.tvAddress.text = shop.address
//       // binding.tvDiscountPerPatti.text = "Rs ${shop.discount_per_petti} per patti"
//
//        binding.tvContactName.text = shop.contact_person
//        binding.tvPhoneNumber.text = shop.phone_number
//
////        binding.boxRate.text = "-"
////        binding.tvReceivable.text = "-"
////        binding.tvLastVisit.text = "-"
////
////        binding.tvCreditLimit.text = "-"
//
//     //   binding.tvInitials.text = getInitials(shop.contact_person)
//
//        val imageUrl = shop.image
//        if (!imageUrl.isNullOrEmpty()) {
//            binding.imgShop.visibility = View.VISIBLE
//          //  binding.imgCamera.visibility = View.GONE
//
//            // Use full URL to show existing image
//            val fullImageUrl = AppConstants.ImageURL + imageUrl
//            Glide.with(this)
//                .load(fullImageUrl)
//                .centerCrop()
//                .placeholder(binding.imgShop.drawable)
//                .into(binding.imgShop)
//        }
//
//
//    }


    private fun bindData(shop: ShopDetailsResponse) {

        // BASIC INFO
        binding.tvShopName.text = shop.name
        binding.tvAddress.text = shop.address
        binding.tvContactName.text = shop.owner_name
        binding.tvPhoneNumber.text = shop.phone_number

        // AREA + LAST VISIT
       // binding.tvAreaName.text = shop.area_name
        binding.tvLastVisit.text = shop.last_visit

        // FINANCIAL INFO
        binding.tvCashIn.text = "${shop.cash_in}"
        binding.tvBorrowed.text = "${shop.borrowed}"
        binding.tvRepaid.text = "${shop.repaid}"
        binding.tvDiscountPerPatti.text = "${shop.discount_per_petti}"

        // DAMAGE / RETURN / LIQUID
        val damage = shop.damage_return

        // EXPIRE
        binding.etExpirePeti.text = damage.expire.peti.toString()
        binding.tvExpireTray.text = damage.expire.tray.toString()
        binding.tvExpireSingle.text = damage.expire.single.toString()

        // RETURN
        binding.tvReturnPeti.text = damage.`return`.peti.toString()
        binding.tvReturnTray.text = damage.`return`.tray.toString()
        binding.tvReturnSingle.text = damage.`return`.single.toString()

        // LIQUID
        binding.tvLiquidPeti.text = damage.liquid.peti.toString()
        binding.tvLiquidTray.text = damage.liquid.tray.toString()
        binding.tvLiquidSingle.text = damage.liquid.single.toString()

        // SHOP IMAGE
        val imageUrl = shop.image
        if (!imageUrl.isNullOrEmpty()) {
            binding.imgShop.visibility = View.VISIBLE

            val fullImageUrl = AppConstants.ImageURL + imageUrl
            Glide.with(this)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(binding.imgShop.drawable)
                .into(binding.imgShop)
        }
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

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
