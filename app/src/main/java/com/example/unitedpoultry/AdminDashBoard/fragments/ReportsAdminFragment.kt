package com.example.unitedpoultry.AdminDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.AdminSettingModule.AdminRateHistoryActivity

import com.example.unitedpoultry.databinding.FragmentReportsAdminBinding


class ReportsAdminFragment : Fragment() {

    private lateinit var binding: FragmentReportsAdminBinding
   // private lateinit var shopAdapter: ShopVisitedAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportsAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardRateHistory.setOnClickListener {
            val intent = Intent(requireContext(), AdminRateHistoryActivity::class.java)
            startActivity(intent)
        }
//
//        binding.cardReceivables.setOnClickListener {
//            val intent = Intent(requireContext(), AdminRiderReportActivity::class.java)
//            startActivity(intent)
//        }

//        binding.cardReceivables.setOnClickListener {
//            val intent = Intent(requireContext(), AdminReceivableReportActivity::class.java)
//            startActivity(intent)
//        }

//        binding.cardAreaReport.setOnClickListener {
//            val intent = Intent(requireContext(), AdminAreaReportActivity::class.java)
//            startActivity(intent)
//        }


//        binding.cardDiscountReport.setOnClickListener {
//            val intent = Intent(requireContext(), AdminDiscountReportActivity::class.java)
//            startActivity(intent)
//        }

//        binding.cardSaleReport.setOnClickListener {
//            val intent = Intent(requireContext(), AdminSalesReportActivity::class.java)
//            startActivity(intent)
//        }

//        val  = listOf(
//            ShopvisitedModel("Jalal Sons", "Last visit: 3 days ago. Rs. 12500. 12 orders", R.drawable.visitedshopimage1),
//            ShopvisitedModel("Al-Fatah Store", "Last visit: 5 days ago. Cash collection", R.drawable.visitedshopimage2),
//            ShopvisitedModel("Green Valley Mart", "Last visit: 2 days ago. Cash collection", R.drawable.visitedshopimage1),
//            ShopvisitedModel("Mini Mart Central", "Last visit: 1 day ago. 9 Boxes. Credit", R.drawable.visitedshopimage2)
//        )
//
//        shopAdapter = ShopVisitedAdapter(historyList.toMutableList())
//        binding.rvVisitedShops.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvVisitedShops.adapter = shopAdapter

//        val assignedShops = 40
//        val visitedShops = 30
//        val remainingShops = 10
//
//        binding.tvAssignedShops.text = "$assignedShops%"
//        binding.pAssignedshops.progress = assignedShops
//
//        binding.tVisitedShops.text = "$visitedShops%"
//        binding.pVisitedShops.progress = visitedShops
//
//        binding.tvRemainingShops.text = "$remainingShops%"
//        binding.pRemainingShops.progress = remainingShops


    }
}
