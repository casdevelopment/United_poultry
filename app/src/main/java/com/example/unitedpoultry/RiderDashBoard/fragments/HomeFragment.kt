package com.example.unitedpoultry.RiderDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.unitedpoultry.R
import com.example.unitedpoultry.Notification.NotificationActivity
import com.example.unitedpoultry.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

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
        binding.toggleStatus.isChecked = false
        updateStatusUI(false)

        // Listen for toggle changes
        binding.toggleStatus.setOnCheckedChangeListener { _, isChecked ->
            updateStatusUI(isChecked)
        }

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

    }

    // Function to update icon and text based on toggle
    private fun updateStatusUI(isOnline: Boolean) {
        if (isOnline) {
            binding.signalStatus.setImageResource(R.drawable.onlinevector)
            binding.tvStatusTitle.text = "You’re Online"
            binding.tvStatusSubtitle.text = "Frequently asked questions"
            binding.todayTargetCard.visibility = View.VISIBLE
            binding.todayTargetCardOffline.visibility = View.GONE
            binding.tvStatesTitle.text = "Today ‘s Stats"

            binding.quickNewImage.setImageResource(R.drawable.quicknewvector)
            binding.quickShopImage.setImageResource(R.drawable.quickshopvector)
            binding.quickHistoryImage.setImageResource(R.drawable.quickhistoryvector)
            binding.quickCollectImage.setImageResource(R.drawable.quickcollectvector)

            binding.quickNewLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.primary20)

            binding.quickCollectLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.sub_primary18)

            binding.quickShopLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.pink15)

            binding.quickHistoryLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green15)



        } else {
            binding.signalStatus.setImageResource(R.drawable.offlinevector)
            binding.tvStatusTitle.text = "You’re Offline"
            binding.tvStatusSubtitle.text = "Toggle to start receiving work"
            binding.todayTargetCard.visibility = View.GONE
            binding.todayTargetCardOffline.visibility = View.VISIBLE

            binding.tvStatesTitle.text = "Yesterday’s Summary"

            binding.quickNewImage.setImageResource(R.drawable.quicknewofflinevector)
            binding.quickShopImage.setImageResource(R.drawable.quickshopofflinevector)
            binding.quickHistoryImage.setImageResource(R.drawable.quickhistoryofflinevector)
            binding.quickCollectImage.setImageResource(R.drawable.quickcollectofflinevector)


            binding.quickNewLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickCollectLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickShopLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)

            binding.quickHistoryLayout.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.black8)
        }
    }
}
