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
import com.example.unitedpoultry.History.Adapter.SaleHistoryDamageAdapter
import com.example.unitedpoultry.databinding.ActivitySaleHistoryBinding
import com.example.unitedpoultry.History.model.SaleHistory
import com.example.unitedpoultry.History.model.SaleItems
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

    private lateinit var expireItemsAdapter: SaleHistoryDamageAdapter

    private lateinit var returnItemsAdapter: SaleHistoryDamageAdapter

    var ReturnD = "return"

    var ExpireD = "expire"

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

        setupDamageRecycler()

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


    private fun setupDamageRecycler() {
        expireItemsAdapter = SaleHistoryDamageAdapter(emptyList(), ExpireD)

        binding.recyclerExpireItems.apply {
            layoutManager = LinearLayoutManager(this@SaleHistoryActivity)
            adapter = expireItemsAdapter
        }

        returnItemsAdapter = SaleHistoryDamageAdapter(emptyList(), ReturnD)

        binding.recyclerReturnItems.apply {
            layoutManager = LinearLayoutManager(this@SaleHistoryActivity)
            adapter = returnItemsAdapter
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
                                    val processedItems = convertSaleItemsToPettiAndTrays(items)

                                    binding.recyclerItems.visibility = View.VISIBLE
                                    itemsAdapter.updateList(processedItems)
                                } else {
                                    binding.recyclerItems.visibility = View.GONE
                                }


                                val expireItems = data.damage_eggs.expire
                                if (!expireItems.isNullOrEmpty()) {
                                    val processedExpireItems = convertItemsToPettiAndTrays(
                                        items = expireItems,
                                        getProductName = { it.product_name },
                                        getQty = { it.qty },
                                        createItem = { name, qty, original ->
                                            original.copy(product_name = name, qty = qty)
                                        }
                                    )

                                    binding.recyclerExpireItems.visibility = View.VISIBLE
                                    binding.tvExpireTitle.visibility = View.VISIBLE
                                    expireItemsAdapter.updateList(processedExpireItems)
                                } else {
                                    binding.recyclerExpireItems.visibility = View.GONE
                                    binding.tvExpireTitle.visibility = View.GONE
                                }


                                val returnItems = data.damage_eggs.`return`
                                if (!returnItems.isNullOrEmpty()) {
                                    val processedReturnItems = convertItemsToPettiAndTrays(
                                        items = returnItems,
                                        getProductName = { it.product_name },
                                        getQty = { it.qty },
                                        createItem = { name, qty, original ->
                                            original.copy(product_name = name, qty = qty)
                                        }
                                    )

                                    binding.recyclerReturnItems.visibility = View.VISIBLE
                                    binding.tvReturnTitle.visibility = View.VISIBLE
                                    returnItemsAdapter.updateList(processedReturnItems)
                                } else {
                                    binding.recyclerReturnItems.visibility = View.GONE
                                    binding.tvReturnTitle.visibility = View.GONE
                                }

                                val liquid = data.damage_eggs.liquid.kg
                                binding.tvLiquid.text = "$liquid kgs"

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


    private fun <T> convertItemsToPettiAndTrays(
        items: List<T>,
        getProductName: (T) -> String,
        getQty: (T) -> Int,
        createItem: (name: String, qty: Int, original: T) -> T
    ): List<T> {
        val result = mutableListOf<T>()
        var pettiCount = 0

        for (item in items) {
            val name = getProductName(item)
            val count = getQty(item)

            if (count <= 0) continue

            if (name.contains("tray", ignoreCase = true)) {
                pettiCount += count / 12
                val remainingTrays = count % 12

                if (remainingTrays > 0) {
                    result.add(createItem(name, remainingTrays, item))
                }
            } else {
                result.add(item)
            }
        }

        if (pettiCount > 0) {
            val firstItem = items.first()
            val pettiItem = createItem("Petti", pettiCount, firstItem)
            result.add(0, pettiItem)
        }

        return result
    }


    private fun convertSaleItemsToPettiAndTrays(items: List<SaleItems>): List<SaleItems> {
        val result = mutableListOf<SaleItems>()

        for (item in items) {
            val count = item.qty
            if (count <= 0) continue

            if (item.product_name.contains("tray", ignoreCase = true)) {
                // Parse single tray price safely
                val rawPrice = item.price.toDoubleOrNull() ?: 0.0

                val pettiCount = count / 12
                val remainingTrays = count % 12

                // Calculate unit price for 1 Petti (12 Trays)
                val pettiUnitPrice = rawPrice * 12.0

                // 1. If Pettis exist, add Petti item first with updated price
                if (pettiCount > 0) {
                    val pettiItem = item.copy(
                        product_name = "Petti",
                        qty = pettiCount,
                        price = formatPrice(pettiUnitPrice)
                    )
                    result.add(pettiItem)
                }

                // 2. If remaining Trays exist, add Tray item with single tray unit price
                if (remainingTrays > 0) {
                    val trayItem = item.copy(
                        qty = remainingTrays,
                        price = formatPrice(rawPrice)
                    )
                    result.add(trayItem)
                }
            } else {
                result.add(item)
            }
        }

        return result
    }

    // Helper to format double price cleanly to String
    private fun formatPrice(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format("%.2f", amount)
        }
    }
}
