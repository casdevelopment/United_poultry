package com.example.unitedpoultry.AdminArea

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.EditAreaRequestModel
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.DeleteAreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.EditAreaViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteRiderViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityEditAreaBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAreaActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAreaBinding
    private val viewModel: EditAreaViewModel by viewModel()
    private val viewModel1: DeleteAreaViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityEditAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )




        showData()

        setupClicks()

    }

    private fun showData(){
        val areaName = intent.getStringExtra("AREA_NAME") ?: ""
        val areaDesc = intent.getStringExtra("AREA_DESC") ?: ""
        val areaCity = intent.getStringExtra("AREA_CITY") ?: ""
        val isActive = intent.getBooleanExtra("AREA_Status", true)

        // Set values to views
        binding.etAreaName.setText(areaName)
        binding.etDescription.setText(areaDesc)
        binding.etCity.setText(areaCity)
        binding.toggleStatus.isChecked = isActive
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.btnSaveArea.setOnClickListener {
            if (validateInputs()) {
                callEditAreaApi()
            }
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
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


    private fun callEditAreaApi() {

        val isActive = binding.toggleStatus.isChecked
        val areaId = intent.getIntExtra("AREA_Id", 0)

        val request = AddAreaRequestModel(
            name = binding.etAreaName.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            is_active = isActive
        )

        viewModel.editArea(areaId, request).observe(this) { apiResponse ->

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
                            setResult(Activity.RESULT_OK)
                            finish()
                        }

                    } else {

                        try {
                            val errorJson = response?.errorBody()?.string()
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, BaseResponse::class.java)
                            Toast.makeText(
                                this,
                                errorResponse?.message ?: "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data
                    if (response != null && response.errorBody() != null) {
                        try {

                            val errorJson = response.errorBody()!!.string()
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, BaseResponse::class.java)
                            Toast.makeText(
                                this, errorResponse?.message ?: apiResponse.message ?: "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                apiResponse.message ?: "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            apiResponse.message ?: "Network Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Shop")
            .setMessage("Are you sure you want to delete this Rider?")
            .setPositiveButton("Yes") { _, _ -> callDeleteShopApi() }
            .setNegativeButton("No", null)
            .show()
    }


    private fun callDeleteShopApi() {
        AppUtil.startLoader(this)

        val areaId = intent.getIntExtra("AREA_Id", 0)

        viewModel1.deleteArea(areaId).observe(this) { response ->
            AppUtil.stopLoader()

            // Only show message if API actually returned a message
            val apiMessage = response.data?.body()?.message
            if (!apiMessage.isNullOrEmpty()) {
                Toast.makeText(this, apiMessage, Toast.LENGTH_SHORT).show()
            }

            // Close activity if success
            if (response.data?.body()?.result == "success") {
                val resultIntent = Intent()
                resultIntent.putExtra("ACTION", "DELETED")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
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
