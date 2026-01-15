package com.example.unitedpoultry.AdminDashBoard.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminArea.Adapter.AdminAreaAdapter
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminArea.viewmodel.AreaViewModel
import com.example.unitedpoultry.AdminDashBoard.BaseLazyFragment
import com.example.unitedpoultry.databinding.FragmentAreasAdminBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

class AreasAdminFragment : BaseLazyFragment() {

    private lateinit var binding: FragmentAreasAdminBinding
    private val viewModel: AreaViewModel by viewModel()
    private lateinit var adapter: AdminAreaAdapter
    private val areaList = mutableListOf<AreaModel>()

    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    private val addEditAreaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Refresh list after add/edit
                areaList.clear()
                currentPage = 1
                fetchAreasFromApi(currentPage)
            }
        }

    override fun onLazyLoad() {
        fetchAreasFromApi(currentPage)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAreasAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFab()
        setupNestedScrollPagination()
    }

    private fun setupRecyclerView() {
        adapter = AdminAreaAdapter(mutableListOf())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreas.layoutManager = layoutManager
        binding.rvAreas.adapter = adapter
        binding.rvAreas.isNestedScrollingEnabled = false // important inside NestedScrollView
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
            showEmptyState(adapter.itemCount == 0)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddNewAreaActivity::class.java)
            addEditAreaLauncher.launch(intent)
        }
    }

    private fun setupNestedScrollPagination() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val view = binding.rvAreas.getChildAt(binding.rvAreas.childCount - 1)
            if (view != null) {
                val diff = (view.bottom - (binding.nestedScrollView.height + scrollY))
                if (diff <= 200 && !isLoading && currentPage < lastPage) {
                    isLoading = true
                    fetchAreasFromApi(currentPage + 1)
                }
            }
        }
    }

    private fun fetchAreasFromApi(page: Int) {
        isLoading = true // prevent duplicate calls
        viewModel.getAreas(page).observe(viewLifecycleOwner) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> if (page == 1) AppUtil.startLoader(requireContext())

                Status.SUCCESS -> {
                    if (page == 1) AppUtil.stopLoader()

                    val response: Response<BaseResponse<com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel>>? =
                        apiResponse.data

                    if (response != null && response.isSuccessful) {
                        val baseResponse = response.body()
                        val areas = baseResponse?.data?.areas
                        val pagination = baseResponse?.data?.pagination

                        // Update pagination info
                        if (pagination != null) lastPage = pagination.last_page

                        if (!areas.isNullOrEmpty()) {
                            if (page == 1) areaList.clear() // reset first page
                            areaList.addAll(areas)
                            adapter.updateList(areaList)
                            showEmptyState(false)
                        } else if (areaList.isEmpty()) {
                            showEmptyState(true)
                        }

                        // Update currentPage only after success
                        currentPage = page
                    } else if (areaList.isEmpty()) {
                        showEmptyState(true)
                    }

                    isLoading = false
                }

                Status.ERROR -> {
                    if (page == 1) AppUtil.stopLoader()
                    isLoading = false
                    if (areaList.isEmpty()) showEmptyState(true)
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAreas.visibility = if (show) View.GONE else View.VISIBLE
    }
}
