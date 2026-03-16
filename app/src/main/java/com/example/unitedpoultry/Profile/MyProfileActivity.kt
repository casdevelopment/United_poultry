package com.example.unitedpoultry.Profile

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityMyProfileBinding
import com.example.unitedpoultry.util.AppConstants.userData
import java.text.SimpleDateFormat
import java.util.Locale


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

//    override fun onResume() {
//        super.onResume()
//        showData()
//    }

    private fun showData(){


        if (userData?.image.isNullOrEmpty()) {

            binding.imgShop.visibility = View.GONE
            binding.tvInitials.visibility = View.VISIBLE

            val name = userData?.name ?: "User Name"

            binding.tvInitials.text = getInitials(name)

        } else {

            binding.imgShop.visibility = View.VISIBLE
            binding.tvInitials.visibility = View.GONE

            Glide.with(this)
                .load(userData?.image)
                .centerCrop()
                .into(binding.imgShop)
        }



        binding.tvUserName.text = userData?.username ?: "User Name"

        binding.tvId.text = "ID: ${userData?.id ?: 0}"

        val type = userData?.role_id ?: 0

        if(type == 2){
            binding.tvUserType.text = "Delivery Rider"
        }else{
            binding.tvUserType.text = "Admin"
        }


        binding.tvFullName.text = userData?.name ?: "User Name"

        binding.tvCnic.text = userData?. cnic ?: "0"

        binding.dateOfJoining.text = formatJoinDate(userData?.created_at ?: "")

        binding.tvContact.text = userData?.phone_number?: "0"

    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }

    fun formatJoinDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }
}
