package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminHome.Adapter.ShopVisitedAdapter
import com.example.unitedpoultry.AdminHome.model.ShopvisitedModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Notification.NotificationActivity
import com.example.unitedpoultry.RiderDashBoard.Home.RiderHomeAdapter
import com.example.unitedpoultry.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var shopAdapter: RiderHomeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize toggle state
        binding.toggleStatus.isChecked = true
        updateStatusUI(true)


        // Listen for toggle changes
        binding.toggleStatus.setOnCheckedChangeListener { _, isChecked ->
            updateStatusUI(isChecked)
        }

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        val historyList = listOf(
            ShopvisitedModel("Jalal Sons", "Last visit: 3 days ago. Rs. 12500. 12 orders", R.drawable.visitedshopimage1),
            ShopvisitedModel("Al-Fatah Store", "Last visit: 5 days ago. Cash collection", R.drawable.visitedshopimage2),
            ShopvisitedModel("Green Valley Mart", "Last visit: 2 days ago. Cash collection", R.drawable.visitedshopimage1),
            ShopvisitedModel("Mini Mart Central", "Last visit: 1 day ago. 9 Boxes. Credit", R.drawable.visitedshopimage2)
        )

        shopAdapter = RiderHomeAdapter(historyList.toMutableList())
        binding.rvVisitedShops.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisitedShops.adapter = shopAdapter

    }

    // Function to update icon and text based on toggle
    private fun updateStatusUI(isOnline: Boolean) {
        if (isOnline) {
            binding.signalStatus.setImageResource(R.drawable.onlinevector)
            binding.tvStatusTitle.text = "You’re Online"
            binding.tvStatusSubtitle.text = "Frequently asked questions"
            binding.todayTargetCard.visibility = View.VISIBLE
            binding.todayTargetCardOffline.visibility = View.GONE
            binding.tvStatesTitle.text = "Todays Payment Stats"

            binding.pendingshops.visibility = View.VISIBLE
            binding.rvVisitedShops.visibility = View.VISIBLE


            binding.quickNewImage.setImageResource(R.drawable.quicknewvector)
            binding.quickShopImage.setImageResource(R.drawable.boxsolidsalesvector)
            binding.quickHistoryImage.setImageResource(R.drawable.quickhistoryvector)
            binding.quickCollectImage.setImageResource(R.drawable.card5)

            binding.quickNewLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.primary20)

            binding.quickCollectLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.sub_primary18)

            binding.quickShopLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.pink15)

            binding.quickHistoryLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green15)

//            binding.rootLayout.background = ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.riderdashboardonlinebackground
//            )

            binding.bgImage.setImageResource(R.drawable.bgfull)


            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)

            // Optional: Change status bar icons to dark if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.window?.decorView?.systemUiVisibility = 0 // light icons: 0, dark icons: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }


        } else {
            binding.signalStatus.setImageResource(R.drawable.offlinevector)
            binding.tvStatusTitle.text = "You’re Offline"
            binding.tvStatusSubtitle.text = "Toggle to start receiving work"
            binding.todayTargetCard.visibility = View.GONE
            binding.todayTargetCardOffline.visibility = View.VISIBLE

            binding.pendingshops.visibility = View.GONE
            binding.rvVisitedShops.visibility = View.GONE

            binding.tvStatesTitle.text = "Todays Payment Stats"

            binding.quickNewImage.setImageResource(R.drawable.quicknewofflinevector)
            binding.quickShopImage.setImageResource(R.drawable.card6)
            binding.quickHistoryImage.setImageResource(R.drawable.quickhistoryofflinevector)
            binding.quickCollectImage.setImageResource(R.drawable.card6)


            binding.quickNewLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickCollectLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickShopLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickHistoryLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

//            binding.rootLayout.background = ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.homerideroffline
//            )

            binding.bgImage.setImageResource(R.drawable.offlinebg)

            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.dark)

            // Optional: Change status bar icons to dark if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.window?.decorView?.systemUiVisibility = 0 // light icons: 0, dark icons: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
