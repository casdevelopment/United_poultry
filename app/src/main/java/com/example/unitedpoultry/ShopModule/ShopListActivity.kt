package com.example.unitedpoultry.ShopModule

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminShopModule.model.ShopModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.Adapter.RiderShopListAdapter
import com.example.unitedpoultry.ShopModule.viewmodel.RiderShopListViewModel
import com.example.unitedpoultry.databinding.ActivityShopListBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShopListActivity : BaseActivity() {

    private lateinit var binding: ActivityShopListBinding
    private lateinit var adapter: RiderShopListAdapter
    private val viewModel: RiderShopListViewModel by viewModel()
    private val viewModel1: UserStatusViewModel by viewModel()

    private val shopList = mutableListOf<ShopModel>()
    private var isLoading = false
    private var currentPage = 1
    private var lastPage = 1
    private var areaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = false, colorResId = R.color.primary)

        val name = intent.getStringExtra("AREA_NAME")
        binding.tvAreaName.text = "$name"
        areaId = intent.getIntExtra("AREA_Id", 0)

        setupRecyclerView()
        setupSearch()
        setupScrollPagination()
        setupClicks()
    }

    // 🔥 ALWAYS refresh list when screen becomes visible
    override fun onResume() {
        super.onResume()
        resetAndFetch()
    }

    private fun setupClicks() {

        binding.backArrow.setOnClickListener {
            finish()
        }

    }

    private fun setupRecyclerView() {
        adapter = RiderShopListAdapter(
            originalList = mutableListOf(),
            areaId = areaId,
            lifecycleOwner = this,
            viewModel = viewModel1

        )


        binding.rvShopList.layoutManager = LinearLayoutManager(this)
        binding.rvShopList.adapter = adapter
        binding.rvShopList.isNestedScrollingEnabled = false
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
                val diff = view.bottom - (binding.nestedScrollView.height + scrollY)
                if (diff <= 200 && !isLoading && currentPage < lastPage) {
                    fetchShops(currentPage + 1)
                }
            }
        }
    }

    // 🔁 Reset pagination + list
    private fun resetAndFetch() {
        shopList.clear()
        adapter.updateList(emptyList())
        currentPage = 1
        lastPage = 1
        fetchShops(1)
    }

    private fun fetchShops(page: Int) {
        isLoading = true

        viewModel.getRiderShops(areaId, page).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    if (page == 1) AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false

                    val body = apiResponse.data?.body()

                    if (body?.result == "success" && body.data != null) {

                        lastPage = body.data.pagination.last_page

                        if (!body.data.shops.isNullOrEmpty()) {

                            if (page == 1) shopList.clear()
                            shopList.addAll(body.data.shops)
                            adapter.updateList(shopList)

                         //   binding.tvShopsCount.text = "${shopList.size} Shops"
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
        binding.layoutEmpty.visibility =
            if (show) android.view.View.VISIBLE else android.view.View.GONE

        binding.rvShopList.visibility =
            if (show) android.view.View.GONE else android.view.View.VISIBLE

       // binding.tvShopsCount.visibility =
          //  if (show) android.view.View.GONE else android.view.View.VISIBLE
    }
}
