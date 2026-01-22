package com.example.unitedpoultry.AdminRiderModule

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderRequestModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.AddRiderViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAddNewAreaBinding
import com.example.unitedpoultry.databinding.ActivityAdminAddNewRiderBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminAddNewRiderActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminAddNewRiderBinding
    private val viewModel: AddRiderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminAddNewRiderBinding.inflate(layoutInflater)
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

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                callAddRiderApi()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etNameError.visibility = View.GONE
        binding.etCnicError.visibility = View.GONE
        binding.etEmailError.visibility = View.GONE
        binding.etPhoneNumberError.visibility = View.GONE
        binding.etAddressError.visibility = View.GONE
        binding.etUserNameError.visibility = View.GONE
        binding.etPasswordError.visibility = View.GONE



        val Name = binding.etName.text.toString().trim()
        if (Name.isEmpty()) {
            binding.etNameError.visibility = View.VISIBLE
            binding.etNameError.text = "Name required"
            valid = false
        } else if (!Name.matches(Regex("^[a-zA-Z ]+$"))) {
            binding.etNameError.visibility = View.VISIBLE
            binding.etNameError.text = "Enter valid name"
            valid = false
        }

        val cnic = binding.etCnic.text.toString().trim()

        if (cnic.isEmpty()) {
            binding.etCnicError.visibility = View.VISIBLE
            binding.etCnicError.text = "Cnic required"
            valid = false
        } else if (!cnic.matches(Regex("^\\d{13}$"))){
            binding.etCnicError.visibility = View.VISIBLE
            binding.etCnicError.text = "Enter valid Cnic"
            valid = false
        }

        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.text = "Email required"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.text = "Enter valid email address"
            valid = false
        }

        val phone = binding.etPhoneNumber.text.toString().trim()

        if (phone.isEmpty()) {
            binding.etPhoneNumberError.visibility = View.VISIBLE
            binding.etPhoneNumberError.text = "Phone number required"
            valid = false
        } else if (!phone.matches(Regex("^\\d{11}$"))){
            binding.etPhoneNumberError.visibility = View.VISIBLE
            binding.etPhoneNumberError.text = "Enter valid phone number"
            valid = false
        }

        val address = binding.etAddress.text.toString().trim()

        if (address.isEmpty()) {
            binding.etAddressError.visibility = View.VISIBLE
            binding.etAddressError.text = "Address required"
            valid = false
        }else if (!address.matches(Regex(".*[a-zA-Z].*"))) {
            binding.etAddressError.visibility = View.VISIBLE
            binding.etAddressError.text = "Enter valid address"
            valid = false
        }

        val username = binding.etUserName.text.toString().trim()

        if (username.isEmpty()) {
            binding.etUserNameError.visibility = View.VISIBLE
            binding.etUserNameError.text = "User Name required"
            valid = false
        } else if (!username.matches(Regex(".*[a-zA-Z].*"))) {
            binding.etUserNameError.visibility = View.VISIBLE
            binding.etUserNameError.text = "Enter valid username"
            valid = false
        }


        val password = binding.etPassword.text.toString().trim()

        if (password.isEmpty()) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password required"
            valid = false
        } else if (password.length < 8) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password must be at least 8 characters"
            valid = false
        }


        return valid
    }

    private fun callAddRiderApi() {

        val isActive = binding.toggleStatus.isChecked

        val request = RiderRequestModel(
            name = binding.etName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            username = binding.etUserName.text.toString().trim(),
            phone_number = binding.etPhoneNumber.text.toString().trim(),
            cnic = binding.etCnic.text.toString().trim(),
            address = binding.etAddress.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            is_active = isActive // ✅ backend expects Int
        )

        viewModel.addRider(request).observe(this) { apiResponse ->

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
