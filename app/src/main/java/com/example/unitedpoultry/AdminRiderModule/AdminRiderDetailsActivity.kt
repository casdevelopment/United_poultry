package com.example.unitedpoultry.AdminRiderModule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderDetailsViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminRiderDetailsBinding
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel


class AdminRiderDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminRiderDetailsBinding
    private val viewModel: RiderDetailsViewModel by viewModel()
    private val filterOptions = arrayOf("Today", "Weekly", "Monthly", "Yearly")


    private var RiderDetails: RiderModel? = null
    private var riderId: Int = 0


    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                when (result.data?.getStringExtra("ACTION")) {

                    "UPDATED" -> {
                        // Just refresh (onResume will handle it)
                    }

                    "DELETED" -> {
                        // Tell List to refresh & close Details
                        val intent = Intent()
                        intent.putExtra("ACTION", "DELETED")
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRiderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(false, R.color.primary)

        riderId = intent.getIntExtra("ID", 0)

        if (riderId == 0) {
            Toast.makeText(this, "Invalid rider id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }



        binding.backArrow.setOnClickListener { finish() }

        binding.editShop.setOnClickListener {

            val rider = RiderDetails
            if (rider == null) {
                Toast.makeText(this, "rider data not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, AdminEditRiderActivity::class.java)
            intent.putExtra("ID", rider.id)
            intent.putExtra("NAME", rider.name)
            intent.putExtra("CNIC", rider.cnic)
            intent.putExtra("EMAIL", rider.email)
            intent.putExtra("PHONE", rider.phone_number)
            intent.putExtra("ADDRESS", rider.address)
            intent.putExtra("USERNAME", rider.username)
            intent.putExtra("PASSWORD", rider.password)



            editLauncher.launch(intent)

        }

        binding.filterCapsule.setOnClickListener {
            val popup = PopupMenu(this, binding.filterCapsule)

            filterOptions.forEach { option ->
                popup.menu.add(option)
            }

            popup.setOnMenuItemClickListener { item ->
                binding.tvFilter.text = item.title
                true
            }

            popup.show()
        }






    }

    override fun onResume() {
        super.onResume()
        fetchShopDetails(riderId)
    }

    private fun fetchShopDetails(riderId: Int) {

        viewModel.getRiderDetails(riderId).observe(this) { apiResponse ->

            when (apiResponse.status) {

                com.example.unitedpoultry.network.Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                com.example.unitedpoultry.network.Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val retrofitResponse = apiResponse.data

                    if (retrofitResponse != null && retrofitResponse.isSuccessful) {

                        val baseResponse = retrofitResponse.body()
                        Toast.makeText(this, baseResponse?.message, Toast.LENGTH_SHORT).show()

                        if (baseResponse?.result == "success") {
                            if (baseResponse?.result == "success") {

                                baseResponse.data?.let {
                                    RiderDetails = it      // ✅ save data
                                    bindData(it)
                                }
                            }
                        }

                    } else {
                        showError(retrofitResponse)
                    }
                }

                com.example.unitedpoultry.network.Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindData(rider: RiderModel) {

        val initials = getInitials(rider.name)
        binding.tvInitials.text = initials
        binding.tvRiderName.text= rider.name
        binding.tvAddress.text = rider.address
        binding.tvPhoneNumber.text = rider.phone_number
        binding.tvCnic.text = rider.cnic
        binding.tvAddress.text = rider.address

        binding.toggleStatus.isChecked = rider.is_active

// Optional: show text
        binding.toggleStatus.text = if (rider.is_active) "Active" else "Inactive"

// Optional: read-only
        binding.toggleStatus.isEnabled = false

    }

    private fun showError(response: retrofit2.Response<*>?) {
        val message = try {
            val errorBody = response?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                Gson().fromJson(
                    errorBody,
                    com.example.unitedpoultry.network.retrofit.BaseResponse::class.java
                ).message
            } else "Something went wrong"
        } catch (e: Exception) {
            "Something went wrong"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun getInitials(name: String?): String {
        if (name.isNullOrBlank()) return ""

        return name
            .trim()
            .split("\\s+".toRegex())
            .take(2) // first two words only
            .joinToString("") { it.first().uppercaseChar().toString() }
    }



}
