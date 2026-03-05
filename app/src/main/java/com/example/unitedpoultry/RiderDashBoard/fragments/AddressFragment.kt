package com.example.unitedpoultry.RiderDashBoard.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
       // setupSearch()
        setupNestedScrollPagination()
    }

    override fun onResume() {
        super.onResume()
        areaList.clear()
        currentPage = 1
        fetchAreasFromApi(currentPage)
    }


    private fun setupRecyclerView() {
        adapter = AreaAdapter(mutableListOf())
        binding.rvAreas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreas.adapter = adapter
        binding.rvAreas.isNestedScrollingEnabled = false
    }

//    private fun setupSearch() {
//        binding.etSearch.addTextChangedListener { editable ->
//            adapter.filter(editable.toString())
//            showEmptyState(adapter.itemCount == 0)
//        }
//    }

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
        isLoading = true
        viewModel.getRiderAreas(page).observe(viewLifecycleOwner) { apiResponse ->
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

                        val totalRecords = pagination?.total_matching_record ?: 0
                        binding.tvAreasCount.text = "$totalRecords areas"

                        if (pagination != null) lastPage = pagination.last_page

                        if (!areas.isNullOrEmpty()) {
                            if (page == 1) areaList.clear()
                            areaList.addAll(areas)
                            adapter.updateList(areaList)
                            showEmptyState(false)
                        } else if (areaList.isEmpty()) {
                            showEmptyState(true)
                        }

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
                    showToast("Network problem")
                }
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvAreas.visibility = if (show) View.GONE else View.VISIBLE
    }
}
