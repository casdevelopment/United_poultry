package com.example.unitedpoultry.AdminArea

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.EditAreaRequestModel
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.EditAreaViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityEditAreaBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAreaActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAreaBinding
    private val viewModel: EditAreaViewModel by viewModel()



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
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.etAreaNameError.visibility = View.GONE


        if (binding.etAreaName.text.toString().trim().isEmpty()) {
            binding.etAreaNameError.visibility = View.VISIBLE
            binding.etAreaNameError.text = "Name required"
            valid = false
        }

        return valid
    }


    private fun callEditAreaApi() {

        val isActive = binding.toggleStatus.isChecked
        val areaId = intent.getIntExtra("AREA_Id", 0)

        val request = EditAreaRequestModel(
            name = binding.etAreaName.text.toString().trim(),
            is_active = isActive // ✅ backend expects Int
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

                        // ✅ Show backend message on success
                        Toast.makeText(this, baseResponse?.message ?: "Success", Toast.LENGTH_LONG).show()

                        // ✅ Close screen only on success
                        if (baseResponse?.result == "success") {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }

                    } else {
                        // ✅ Show backend message even if HTTP is not successful
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
                            // ✅ Parse errorBody and show message from backend
                            val errorJson = response.errorBody()!!.string()
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(errorJson, BaseResponse::class.java)
                            Toast.makeText(
                                this,
                                errorResponse?.message ?: apiResponse.message ?: "Something went wrong",
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


    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }
}
