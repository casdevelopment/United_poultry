package com.example.unitedpoultry.AdminSettingModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminSettingModule.DataModel.HistoryData
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.AdminSettingModule.SettingHomeActivity
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditProfileBinding
import com.example.unitedpoultry.databinding.ActivityAdminRateManagmentBinding
import com.example.unitedpoultry.network.Status.*
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response
import kotlin.getValue


class AdminRateManagmentActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRateManagmentBinding
    private val viewModel: AdminSettingViewModel by viewModel()
    private var page = 1
    private lateinit var  historyListData:  List<HistoryData>
    private var  ratesList = mutableListOf <RatesData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminRateManagmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


//        val name = intent.getStringExtra("name") ?: ""
//        val address = intent.getStringExtra("address") ?: ""
//        val discount = intent.getStringExtra("Discount") ?: ""


//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvStatus.text = status



        binding.btnSave.setOnClickListener {

            val intent = Intent(this, SettingHomeActivity::class.java)
            startActivity(intent)
        }


        binding.backArrow.setOnClickListener {
            finish()
        }
        getRateHistory()

    }
    private fun getRateHistory() {
        viewModel.rateHistory(page).observe(this@AdminRateManagmentActivity){ serverResponse->
            when(serverResponse.status){
                SUCCESS -> {
                    AppUtil.stopLoader()

                    if (serverResponse.data != null && serverResponse.data.isSuccessful) {
                        val baseResponse = serverResponse.data.body()
                        showToast(baseResponse?.message ?: "")
                         historyListData = baseResponse?.data?.history!!
                        if(historyListData.isNotEmpty()){
                            for (ratesItem in historyListData) {
                            val date=  ratesItem.date
                                for (item in ratesItem.rates!!) {
                                    ratesList.add(RatesData(id = item.id,
                                        product_id = item.product_id,
                                        product_name = item.product_name,
                                        packing = item.packing,
                                        eggs_count = item.eggs_count,
                                        price =item.price ,
                                        date = date))
                                }
                            }
                            for (ratesListItem in ratesList) {
                                Log.v("getRateHistory", "price: ${ratesListItem.price}")
                                Log.v("getRateHistory", "product_name: ${ratesListItem.product_name}")
                                Log.v("getRateHistory", "date: ${ratesListItem.date}")
                            }
                            Log.v("getRateHistory", "getRateHistory: ${ratesList.size}")
                        }

                    }

                }
                ERROR -> {
                    AppUtil.stopLoader()
                    showToast(serverResponse.message.toString())
                }
                LOADING -> {
                    AppUtil.startLoader(this@AdminRateManagmentActivity)
                }
            }

        }
    }
}
