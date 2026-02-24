package com.example.unitedpoultry.AdminArea

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAddNewAreaBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNewAreaActivity : BaseActivity() {

    private lateinit var binding: ActivityAddNewAreaBinding
    private val viewModel: AddAreaViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        setupClicks()
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener {
            finish() }

        binding.btnCancel.setOnClickListener {
            finish() }

        binding.btnSaveArea.setOnClickListener {
            if (validateInputs()) {
                callAddAreaApi()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etAreaNameError.visibility = View.GONE
        binding.etCityError.visibility = View.GONE
        binding.etDescriptionError.visibility = View.GONE


        val Name = binding.etAreaName.text.toString().trim()
        if (Name.isEmpty()) {
            binding.etAreaNameError.visibility = View.VISIBLE
            binding.etAreaNameError.text = "Name required"
            valid = false
        }
//        else if (!Name.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etAreaNameError.visibility = View.VISIBLE
//            binding.etAreaNameError.text = "Enter valid area name"
//            valid = false
//        }
//
//        val description = binding.etDescription.text.toString().trim()
//        if (description.isEmpty()) {
//            binding.etDescriptionError.visibility = View.VISIBLE
//            binding.etDescriptionError.text = "Description required"
//            valid = false
//        } else if (!description.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etDescriptionError.visibility = View.VISIBLE
//            binding.etDescriptionError.text = "Enter valid Description"
//            valid = false
//        }
//
//
//
//        val city = binding.etCity.text.toString().trim()
//        if (city.isEmpty()) {
//            binding.etCityError.visibility = View.VISIBLE
//            binding.etCityError.text = "City required"
//            valid = false
//        } else if (!city.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etCityError.visibility = View.VISIBLE
//            binding.etCityError.text = "Enter valid city name"
//            valid = false
//        }

        return valid
    }

    private fun callAddAreaApi() {

        val isActive = binding.toggleStatus.isChecked

        val request = AddAreaRequestModel(
            name = binding.etAreaName.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            is_active = isActive
        )

        viewModel.addArea(request).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data
                    if (response != null && response.isSuccessful) {

                        val baseResponse = response.body()

                        Toast.makeText(this, baseResponse?.message ?: "Success", Toast.LENGTH_LONG).show()

                        if (baseResponse?.result == "success") {
                            finish()
                        }

                    } else {

                        try {
                            val errorJson = response?.errorBody()?.string()
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, BaseResponse::class.java)

                            Toast.makeText(this, errorResponse?.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()

                        } catch (e: Exception) {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data
                    if (response != null && response.errorBody() != null) {
                        try {
                            // ✅ Parse errorBody and show message from backend
                            val errorJson = response.errorBody()!!.string()
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, BaseResponse::class.java)

                            Toast.makeText(this, errorResponse?.message ?: apiResponse.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()

                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                apiResponse.message ?: "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, apiResponse.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }
}
