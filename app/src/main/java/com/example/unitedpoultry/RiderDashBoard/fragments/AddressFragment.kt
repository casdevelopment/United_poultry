package com.example.unitedpoultry.RiderDashBoard.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.RiderArea.Adapter.AreaAdapter
import com.example.unitedpoultry.RiderArea.viewmodel.RiderAreaViewModel
import com.example.unitedpoultry.databinding.FragmentAddressBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val viewModel: RiderAreaViewModel by viewModel()
    private lateinit var adapter: AreaAdapter
    private val areaList = mutableListOf<AreaModel>()

    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    private var currentQuery = ""
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setupRecyclerView()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        resetAndFetch()
    }

    private fun setupRecyclerView() {
        adapter = AreaAdapter(mutableListOf())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreas.layoutManager = layoutManager
        binding.rvAreas.adapter = adapter

        // Pagination scroll listener
        binding.rvAreas.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0 || isLoading || isSearching) return

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3
                    && firstVisibleItemPosition >= 0
                    && currentPage < lastPage
                ) {
                    isLoading = true
                    fetchAreasFromApi(currentPage + 1)
                }
            }
        })
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            currentQuery = editable.toString()
            isSearching = currentQuery.isNotEmpty()

            adapter.filter(currentQuery)
            showEmptyState(adapter.itemCount == 0)
        }
    }

    private fun resetAndFetch() {
        areaList.clear()
        currentPage = 1
        fetchAreasFromApi(currentPage)
    }

    private fun fetchAreasFromApi(page: Int) {
        isLoading = true
        viewModel.getRiderAreas(page).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> if (page == 1) AppUtil.startLoader(requireContext())

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()

                    val response = apiResponse.data
                    val baseResponse = response?.body()
                    val areas = baseResponse?.data?.areas
                    val pagination = baseResponse?.data?.pagination

                    val totalRecords = pagination?.total_matching_record ?: 0
                    binding.tvAreasCount.text = "$totalRecords areas"
                    if (pagination != null) lastPage = pagination.last_page

                    if (!areas.isNullOrEmpty()) {
                        if (page == 1) areaList.clear()
                        areaList.addAll(areas)
                        adapter.updateList(areaList)
                        adapter.filter(currentQuery)
                        showEmptyState(adapter.itemCount == 0)
                    } else if (areaList.isEmpty()) {
                        showEmptyState(true)
                    }

                    currentPage = page
                    isLoading = false
                }

                Status.ERROR -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false
                    if (areaList.isEmpty()) showEmptyState(true)
                    showToast("Network connection problem. Please try again.")
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAreas.visibility = if (show) View.GONE else View.VISIBLE
    }
}
