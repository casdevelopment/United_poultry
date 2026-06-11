package com.example.unitedpoultry.History

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.R
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.History.Adapter.SaleHistoryAdapter
import com.example.unitedpoultry.databinding.ActivitySaleHistoryBinding
import com.example.unitedpoultry.History.model.SaleHistory
import com.example.unitedpoultry.History.viewmodel.SaleHistoryDetailViewModel
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SaleHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleHistoryBinding

    private val viewModel: SaleHistoryDetailViewModel by viewModel()

    private var id: Int = 0

    private var fullImageUrl: String? = null

    private lateinit var itemsAdapter: SaleHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.primary
        )

        binding = ActivitySaleHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getIntExtra("ID", 0)

        setupRecycler()

        onclick()


    }

    private fun onclick(){
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.ivPaymentSlip.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", fullImageUrl)
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        loadSaleDetail()
    }

    private fun setupRecycler() {
        itemsAdapter = SaleHistoryAdapter(emptyList())

        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(this@SaleHistoryActivity)
            adapter = itemsAdapter
        }
    }


    private fun loadSaleDetail() {

        viewModel.getSaleDetail(id)
            .observe(this) { response ->

                when (response.status) {

                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                    }

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()

                        val res = response.data
                        if (res != null && res.isSuccessful) {

                            val baseResponse = res.body() as BaseResponse<SaleHistory>?

                            baseResponse?.data?.let { data ->

                                binding.tvShopName.text = data.shop_name
                                binding.tvShopAddress.text = data.area_name

                                binding.tvInitials.text = getInitials(data.shop_name)

                                binding.tvDate.text = data.sale_date

                                binding.tvPaymentType.text = data.payment_type
                                binding.tvDiscount.text = "Rs ${formatAmountString(data.discount)}"
                                binding.tvTotal.text = "Rs ${formatAmountString(data.total)}"


                                binding.tvBorrowed.text = "Rs ${data.borrowed_amount}"
                                binding.tvCollection.text = "Rs ${data.collection_amount}"


                                val items = data.items

                                if (!items.isNullOrEmpty()) {
                                    binding.recyclerItems.visibility = View.VISIBLE
                                    itemsAdapter.updateList(items)
                                } else {
                                    binding.recyclerItems.visibility = View.GONE
                                }



                                val imageUrl = data.payment_record_url
                                if (!imageUrl.isNullOrEmpty()) {
                                    binding.slipLayout.visibility = View.VISIBLE

                                    fullImageUrl = AppConstants.ImageURL + imageUrl

                                    Glide.with(this)
                                        .load(fullImageUrl)
                                        .placeholder(binding.ivPaymentSlip.drawable)
                                        .into(binding.ivPaymentSlip)

                                }

                                if (data.payment_note != null) {
                                    binding.noteLayout.visibility = View.VISIBLE
                                    binding.etNote1.text = data.payment_note

                                }

                            }
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }

    fun getInitials(name: String): String {
        return name
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.first().uppercaseChar() }
            .joinToString("")
    }



    fun formatAmountString(amount: String): String {
        val value = amount.toDoubleOrNull() ?: 0.0
        return if (value % 1.0 == 0.0) {
            "%.0f".format(value)
        } else {
            "%.2f".format(value)
        }
    }
}
