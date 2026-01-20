package com.example.unitedpoultry.AdminDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminRiderModule.Adapter.AdminRidersAdapter
import com.example.unitedpoultry.AdminRiderModule.AdminAddNewRiderActivity
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentRiderAdminBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

class RiderAdminFragment : Fragment() {

    private lateinit var binding: FragmentRiderAdminBinding
    private val viewModel: RiderViewModel by viewModel()

    private lateinit var adapter: AdminRidersAdapter
    private val riderList = mutableListOf<RiderModel>()

    private var currentFilter = "ALL"
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiderAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        setupNestedScrollPagination()

        // Highlight All filter by default on fragment launch
        applyFilter("ALL")
        setupFab()

    }

    override fun onResume() {
        super.onResume()
        riderList.clear()
        currentPage = 1
        fetchRidersFromApi(currentPage)
    }

    private fun setupRecyclerView() {
        adapter = AdminRidersAdapter(mutableListOf())
        binding.rvRiders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiders.adapter = adapter
        binding.rvRiders.isNestedScrollingEnabled = false
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AdminAddNewRiderActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupSearchAndFilters() {
        // Search listener
        binding.etSearch.addTextChangedListener {
            adapter.filter(it.toString(), currentFilter)
            updateFilterCounts()
        }

        // Filter click listeners
        binding.filterAll.setOnClickListener { applyFilter("ALL") }
        binding.filterActive.setOnClickListener { applyFilter("Active") }
        binding.filterInactive.setOnClickListener { applyFilter("Inactive") }
    }

    private fun applyFilter(type: String) {
        currentFilter = type
        adapter.filter(binding.etSearch.text.toString(), type)
        highlightFilter(type)
        updateFilterCounts()
    }

    private fun updateFilterCounts() {
        binding.filterAll.text = "All (${adapter.countByStatus("ALL")})"
        binding.filterActive.text = "Active (${adapter.countByStatus("Active")})"
        binding.filterInactive.text = "Inactive (${adapter.countByStatus("Inactive")})"
    }

    private fun highlightFilter(type: String) {
        val filters = listOf(
            binding.filterAll,
            binding.filterActive,
            binding.filterInactive
        )

        // Reset all filters
        filters.forEach {
            it.setBackgroundResource(R.drawable.filter_bg)
            it.setTextColor(requireContext().getColor(R.color.black))
        }

        // Highlight selected filter
        val selected = when (type) {
            "Active" -> binding.filterActive
            "Inactive" -> binding.filterInactive
            else -> binding.filterAll
        }

        selected.setBackgroundResource(R.drawable.filter_bg_selected)
        selected.setTextColor(requireContext().getColor(R.color.white))
    }

    private fun setupNestedScrollPagination() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val view = binding.rvRiders.getChildAt(binding.rvRiders.childCount - 1)
            if (view != null) {
                val diff = view.bottom - (binding.nestedScrollView.height + scrollY)
                if (diff <= 200 && !isLoading && currentPage < lastPage) {
                    isLoading = true
                    fetchRidersFromApi(currentPage + 1)
                }
            }
        }
    }

    private fun fetchRidersFromApi(page: Int) {
        isLoading = true

        viewModel.getRiders(page).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> if (page == 1) AppUtil.startLoader(requireContext())

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()

                    val response: Response<BaseResponse<RiderDataResponceModel>>? =
                        apiResponse.data

                    if (response != null && response.isSuccessful) {
                        val baseResponse = response.body()
                        val riders = baseResponse?.data?.sellers
                        val pagination = baseResponse?.data?.pagination

                        if (pagination != null) lastPage = pagination.last_page

                        if (!riders.isNullOrEmpty()) {
                            if (page == 1) riderList.clear()
                            riderList.addAll(riders)
                            adapter.updateList(riderList)
                            showEmptyState(false)

                            // Update counts immediately
                            updateFilterCounts()
                        } else if (riderList.isEmpty()) {
                            showEmptyState(true)
                            binding.filterAll.text = "All (0)"
                        }

                        currentPage = page
                    } else if (riderList.isEmpty()) {
                        showEmptyState(true)
                        binding.filterAll.text = "All (0)"
                    }

                    isLoading = false
                }

                Status.ERROR -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false
                    if (riderList.isEmpty()) showEmptyState(true)
                    binding.filterAll.text = "All (0)"
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvRiders.visibility = if (show) View.GONE else View.VISIBLE
    }
}
