package com.example.unitedpoultry.ShopModule

import android.os.Bundle
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var currentFilter = "all"


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

    override fun onResume() {
        super.onResume()
        resetAndFetch()
    }

    private fun setupClicks() {

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.filterAll.setOnClickListener {
            applyFilter("all")
        }

        binding.filterVisited.setOnClickListener {
            applyFilter("visited")
        }

        binding.filterPending.setOnClickListener {
            applyFilter("pending")
        }

    }

    private fun applyFilter(filter: String) {
        if (currentFilter == filter) return

        currentFilter = filter
        highlightSelectedFilter(filter)
        resetAndFetch()
    }

    private fun highlightSelectedFilter(filter: String) {

        styleFilter(binding.filterAll, filter == "all")
        styleFilter(binding.filterVisited, filter == "visited")
        styleFilter(binding.filterPending, filter == "pending")
    }

    private fun styleFilter(view: TextView, isSelected: Boolean) {
        if (isSelected) {
            view.setTextColor(resources.getColor(android.R.color.white))
            view.setBackgroundResource(R.drawable.filter_bg_selected)
        } else {
            view.setTextColor(resources.getColor(R.color.black60))
            view.setBackgroundResource(R.drawable.filter_bg)
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

    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
            showEmptyState(adapter.itemCount == 0)
        }
    }

    private fun setupScrollPagination() {
        val layoutManager = binding.rvShopList.layoutManager as LinearLayoutManager
        binding.rvShopList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return // only scroll down

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Load more when near the end
                if (!isLoading && currentPage < lastPage &&
                    (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 3)
                ) {
                    fetchShops(currentPage + 1)
                }
            }
        })
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

        viewModel.getRiderShops(areaId, page,currentFilter).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    if (page == 1) AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false

                    val body = apiResponse.data?.body()

                    if (body?.result == "success" && body.data != null) {

                        val counts = body.data.filter_counts

                        binding.filterAll.text = "All(${counts.all})"
                        binding.filterVisited.text = "Visited(${counts.visited})"
                        binding.filterPending.text = "Pending(${counts.pending})"

                        binding.capsuleText.text = "${body.data.total_shops_assigned} Shops Assigned"

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
                       // showToast(body?.message ?: "No shops found")
                    }
                }

                Status.ERROR -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false
                    if (shopList.isEmpty()) showEmptyState(true)
                    //showToast(apiResponse.message ?: "Network error")
                    showToast("Network problem")
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
