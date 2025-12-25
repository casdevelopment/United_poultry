package com.example.unitedpoultry.AdminDashBoard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminHome.Adapter.ShopVisitedAdapter
import com.example.unitedpoultry.AdminHome.model.ShopvisitedModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentHomeAdminBinding


class HomeAdminFragment : Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding
    private lateinit var shopAdapter: ShopVisitedAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyList = listOf(
            ShopvisitedModel("Jalal Sons", "Last visit: 3 days ago. Rs. 12500. 12 orders", R.drawable.visitedshopimage1),
            ShopvisitedModel("Al-Fatah Store", "Last visit: 5 days ago. Cash collection", R.drawable.visitedshopimage2),
            ShopvisitedModel("Green Valley Mart", "Last visit: 2 days ago. Cash collection", R.drawable.visitedshopimage1),
            ShopvisitedModel("Mini Mart Central", "Last visit: 1 day ago. 9 Boxes. Credit", R.drawable.visitedshopimage2)
        )

        shopAdapter = ShopVisitedAdapter(historyList.toMutableList())
        binding.rvVisitedShops.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisitedShops.adapter = shopAdapter

        val assignedShops = 40
        val visitedShops = 30
        val remainingShops = 10

        binding.tvAssignedShops.text = "$assignedShops%"
        binding.pAssignedshops.progress = assignedShops

        binding.tVisitedShops.text = "$visitedShops%"
        binding.pVisitedShops.progress = visitedShops

        binding.tvRemainingShops.text = "$remainingShops%"
        binding.pRemainingShops.progress = remainingShops


    }
}
