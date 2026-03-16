package com.example.unitedpoultry.Profile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Profile.ViewModel.ChangePasswordViewModel
import com.example.unitedpoultry.Profile.model.ChangePasswordRequestModel
import com.example.unitedpoultry.databinding.ActivityChangePasswordBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
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

//        binding.btnCancel.setOnClickListener {
//            finish() }

        binding.btnUpdatePassword.setOnClickListener {
            if (validateInputs()) {
                changePasswordApi()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etCurrentPasswordError.visibility = View.GONE
        binding.etNewPasswordError.visibility = View.GONE
        binding.etConfirmPasswordError.visibility = View.GONE


        val currentPassword = binding.etCurrentPassword.text.toString().trim()

        if (currentPassword.isEmpty()) {
            binding.etCurrentPasswordError.visibility = View.VISIBLE
            binding.etCurrentPasswordError.text = "Enter your password"
            valid = false
        }
//        else if (currentPassword.length < 8) {
//            binding.etCurrentPasswordError.visibility = View.VISIBLE
//            binding.etCurrentPasswordError.text = "Password must be at least 8 characters"
//            valid = false
//        }


        val newPassword = binding.etNewPassword.text.toString().trim()

        if (newPassword.isEmpty()) {
            binding.etNewPasswordError.visibility = View.VISIBLE
            binding.etNewPasswordError.text = "Enter new Password"
            valid = false
        } else if (newPassword.length < 8) {
            binding.etNewPasswordError.visibility = View.VISIBLE
            binding.etNewPasswordError.text = "New Password must be at least 8 characters"
            valid = false
        }


        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            binding.etConfirmPasswordError.text = "Enter password again"
            valid = false
        } else if (confirmPassword != newPassword) {
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            binding.etConfirmPasswordError.text = "Password mismatch"
            valid = false
        }

        return valid
    }

    private fun changePasswordApi() {


        val request = ChangePasswordRequestModel(
            current_password = binding.etCurrentPassword.text.toString().trim(),
            new_password = binding.etNewPassword.text.toString().trim(),
            new_password_confirmation = binding.etConfirmPassword.text.toString().trim(),
        )

        viewModel.changePassword(request).observe(this) { apiResponse ->

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
