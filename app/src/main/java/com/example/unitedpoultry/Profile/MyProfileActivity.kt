package com.example.unitedpoultry.Profile

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityMyProfileBinding
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppConstants.userData


class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        binding.backArrow.setOnClickListener {
            finish()
        }



    }

    override fun onResume() {
        super.onResume()
        showData()
    }

    private fun showData(){

        binding.tvUserName.text = userData?.username ?: "User Name"
        binding.capsuleText.text = if (userData?.is_active == true) {
            "Active"
        } else {
            "Inactive"
        }


        //  binding.tvInitials.text = getInitials(userData?.username ?: "User Name")

        binding.tvFullName.text = userData?.name ?: "User Name"

        binding.tvCnic.text = userData?. cnic ?: "0"

        binding.dateOfBirth.text = "-"

        binding.dateOfJoining.text = "-"

        binding.tvContact.text = userData?.phone_number?: "0"

        binding.tvEmergencyContact.text = "-"

        binding.tvAddress.text = userData?.address?: "0"



        val imageUrl = userData?.image
        if (!imageUrl.isNullOrEmpty()) {
            binding.imgShop.visibility = View.VISIBLE
            //  binding.imgCamera.visibility = View.GONE

            // Use full URL to show existing image
            val fullImageUrl = AppConstants.ImageURL + imageUrl
            Glide.with(this)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(binding.imgShop.drawable)
                .into(binding.imgShop)
        }


    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
