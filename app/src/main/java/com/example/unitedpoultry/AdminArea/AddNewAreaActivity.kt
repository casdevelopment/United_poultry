package com.example.unitedpoultry.AdminArea

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAddNewAreaBinding
import com.example.unitedpoultry.network.Status
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
            setResult(Activity.RESULT_OK)
            finish() }

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_OK)
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

        if (binding.etAreaName.text.toString().trim().isEmpty()) {
            binding.etAreaNameError.visibility = View.VISIBLE
            binding.etAreaNameError.text = "Name required"
            valid = false
        }

        if (binding.etCity.text.toString().trim().isEmpty()) {
            binding.etCityError.visibility = View.VISIBLE
            binding.etCityError.text = "City required"
            valid = false
        }

        return valid
    }

    private fun callAddAreaApi() {

        val isActive = binding.toggleStatus.isChecked

        val request = AddAreaRequestModel(
            name = binding.etAreaName.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            is_active = isActive // ✅ backend expects Int
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

                        // ✅ Always show backend message
                        Toast.makeText(this, baseResponse?.message ?: "Success", Toast.LENGTH_LONG).show()

                        // ✅ Close screen only on success
                        if (baseResponse?.result == "success") {

                            setResult(Activity.RESULT_OK)
                            finish()
                        }

                    } else {
                        Toast.makeText(this,
                            "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message ?: "Network Error", Toast.LENGTH_SHORT).show()
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
