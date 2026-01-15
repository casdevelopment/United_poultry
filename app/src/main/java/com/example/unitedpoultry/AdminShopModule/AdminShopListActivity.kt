package com.example.unitedpoultry.AdminShopModule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminShopModule.Adapter.AdminShopListAdapter
import com.example.unitedpoultry.AdminShopModule.model.ShopModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.ShopListViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminShopListBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminShopListActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminShopListBinding
    private lateinit var adapter: AdminShopListAdapter
    private val viewModel: ShopListViewModel by viewModel()

    private val shopList = mutableListOf<ShopModel>()
    private var isLoading = false
    private var currentPage = 1
    private var lastPage = 1
    private var areaId: Int = 0

    private val addEditShopLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                reloadShops()
            }
        }

    private fun reloadShops() {
        shopList.clear()
        adapter.updateList(emptyList())
        currentPage = 1
        lastPage = 1
        fetchShops(currentPage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = false, colorResId = R.color.primary)

        val name = intent.getStringExtra("AREA_NAME")
        binding.tvAreaName.text = "$name Shops"
        areaId = intent.getIntExtra("AREA_Id", 0)

        setupRecyclerView()
        setupSearch()
        setupScrollPagination()

        fetchShops(currentPage)
        clickListeners()
    }

    private fun clickListeners() {
        binding.backArrow.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AdminAddNewShopActivity::class.java)
            intent.putExtra("AREA_ID", areaId.toString())
            addEditShopLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminShopListAdapter(
            originalList = mutableListOf(),
            areaId = areaId
        )
        binding.rvShopList.layoutManager = LinearLayoutManager(this)
        binding.rvShopList.adapter = adapter
        binding.rvShopList.isNestedScrollingEnabled = false // important for NestedScrollView
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
            showEmptyState(adapter.itemCount == 0)
        }
    }

    private fun setupScrollPagination() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val view = binding.rvShopList.getChildAt(binding.rvShopList.childCount - 1)
            if (view != null) {
                val diff = (view.bottom - (binding.nestedScrollView.height + scrollY))
                if (diff <= 200 && !isLoading && currentPage < lastPage) {
                    isLoading = true
                    fetchShops(currentPage + 1)
                }
            }
        }
    }

    private fun fetchShops(page: Int) {
        isLoading = true

        viewModel.getShops(areaId, page).observe(this) { apiResponse ->
            when (apiResponse.status) {

                Status.LOADING -> {
                    if (page == 1) AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false

                    val body = apiResponse.data?.body()

                    if (body?.result == "success" && body.data != null) {
                        // Correctly set pagination
                        lastPage = body.data.pagination.last_page

                        if (!body.data.shops.isNullOrEmpty()) {
                            if (page == 1) shopList.clear() // reset first page
                            shopList.addAll(body.data.shops)
                            adapter.updateList(shopList)
                            binding.tvShopsCount.text = "${shopList.size} Shops"
                            showEmptyState(false)
                        } else if (shopList.isEmpty()) {
                            showEmptyState(true)
                        }

                        currentPage = page
                    } else {
                        if (shopList.isEmpty()) showEmptyState(true)
                        showToast(body?.message ?: "No shops found")
                    }
                }

                Status.ERROR -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false
                    if (shopList.isEmpty()) showEmptyState(true)
                    showToast(apiResponse.message ?: "Network error")
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvShopList.visibility = if (show) android.view.View.GONE else android.view.View.VISIBLE
        binding.tvShopsCount.visibility = if (show) android.view.View.GONE else android.view.View.VISIBLE
    }
}
