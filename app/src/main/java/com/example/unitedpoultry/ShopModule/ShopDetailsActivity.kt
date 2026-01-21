package com.example.unitedpoultry.ShopModule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.CollectionformActivity
import com.example.unitedpoultry.NewSale.SaleFormActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.Adapter.RecentActivityAdapter
import com.example.unitedpoultry.ShopModule.model.RecentActivityModel
import com.example.unitedpoultry.ShopModule.viewmodel.RiderShopDetailsViewModel
import com.example.unitedpoultry.databinding.ActivityShopDetailsBinding
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShopDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityShopDetailsBinding
    private val viewModel: RiderShopDetailsViewModel by viewModel()

    private var shopDetails: ShopDetailsResponseModel? = null
    private var shopId: Int = 0

    private lateinit var adapter: RecentActivityAdapter
    private val activityList = mutableListOf<RecentActivityModel>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(false, R.color.primary)

        // areaId = intent.getIntExtra("AREA_ID", 0)
        shopId = intent.getIntExtra("ID", 0)

        if (shopId == 0) {
            Toast.makeText(this, "Invalid shop id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        binding.backArrow.setOnClickListener { finish() }

        binding.btnNewSale.setOnClickListener {
            shopDetails?.let { shop ->

                val intent = Intent(this, SaleFormActivity::class.java)
                intent.putExtra("ID", shop.id)
                intent.putExtra("NAME", shop.name)
                intent.putExtra("ADDRESS", shop.address)
                intent.putExtra("DISCOUNT", shop.discount_per_petti)
                startActivity(intent)

            } ?: run {
                Toast.makeText(this, "Shop details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCollectPayment.setOnClickListener {
            shopDetails?.let { shop ->

                val intent = Intent(this, CollectionformActivity::class.java)
                intent.putExtra("ID", shop.id)
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

        // ✅ Sample Data
        activityList.add(
            RecentActivityModel("Purchase", "3 days ago, 10:35 PM", "cash", 1000)
        )
        activityList.add(
            RecentActivityModel("Payment Received", "2 days ago, 02:15 PM", "credit", 5000)
        )
        activityList.add(
            RecentActivityModel("Purchase", "1 day ago, 11:20 AM", "cash", 2000)
        )

        adapter = RecentActivityAdapter(this, activityList)
        binding.recyclerRecentActivities.adapter = adapter



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

    private fun bindData(shop: ShopDetailsResponseModel) {

        binding.tvShopName.text = shop.name
        binding.tvAddress.text = shop.address
        binding.tvDiscount.text = "${shop.discount_per_petti} %"

        binding.tvContactName.text = shop.contact_person
        binding.tvPhoneNumber.text = shop.phone_number

        binding.boxRate.text = "-"
        binding.tvReceivable.text = "-"
        binding.tvLastVisit.text = "-"

        binding.tvCreditLimit.text = "-"

        binding.tvInitials.text = getInitials(shop.contact_person)


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
