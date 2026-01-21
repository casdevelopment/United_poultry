package com.example.unitedpoultry.Profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.databinding.ActivityMyProfileBinding
import com.example.unitedpoultry.util.AppConstants.userData
import org.koin.android.ext.android.inject

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


        showData()

        binding.backArrow.setOnClickListener {
            finish()
        }



    }

    private fun showData(){

        binding.tvUserName.text = userData?.username ?: "User Name"
        binding.capsuleText.text = if (userData?.is_active == true) {
            "Active"
        } else {
            "Inactive"
        }


        binding.tvInitials.text = getInitials(userData?.username ?: "User Name")

        binding.tvFullName.text = userData?.name ?: "User Name"

        binding.tvCnic.text = userData?. cnic ?: "0"

        binding.dateOfBirth.text = "-"

        binding.dateOfJoining.text = "-"

        binding.tvContact.text = userData?.phone_number?: "0"

         binding.tvEmergencyContact.text = "-"

        binding.tvAddress.text = userData?.address?: "0"

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
