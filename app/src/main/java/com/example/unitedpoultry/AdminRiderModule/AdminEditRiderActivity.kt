package com.example.unitedpoultry.AdminRiderModule


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.EditRiderViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteRiderViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditRiderBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminEditRiderActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditRiderBinding
    private val viewModel: EditRiderViewModel by viewModel()
    private val viewModel1: DeleteRiderViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAdminEditRiderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )




        showData()

        setupClicks()

    }

    private fun showData(){
        val name = intent.getStringExtra("NAME") ?: ""
        val cnic = intent.getStringExtra("CNIC") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val phone = intent.getStringExtra("PHONE") ?: ""
        val address = intent.getStringExtra("ADDRESS") ?: ""
        val username = intent.getStringExtra("USERNAME") ?: ""
        val password = intent.getStringExtra("PASSWORD") ?: ""



        // Set values to views
        binding.etName.setText(name)
        binding.etCnic.setText(cnic)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phone)
        binding.etAddress.setText(address)
        binding.etUserName.setText(username)
        binding.etPassword.setText(password)

    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                callEditAreaApi()
            }
        }

        binding.btnDeleteShops.setOnClickListener {
            showDeleteConfirmation()
        }
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.etNameError.visibility = View.GONE
        binding.etAddressError.visibility = View.GONE
        binding.etPasswordError.visibility = View.GONE


        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.etNameError.visibility = View.VISIBLE
            binding.etNameError.text = "Name required"
            valid = false
        }

        if (binding.etAddress.text.toString().trim().isEmpty()) {
            binding.etAddressError.visibility = View.VISIBLE
            binding.etAddressError.text = "Address required"
            valid = false
        }

        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password required"
            valid = false
        }

        return valid
    }


    private fun callEditAreaApi() {

        val id = intent.getIntExtra("ID", 0)

        val request = RiderEditRequestModel(
            name = binding.etName.text.toString().trim(),
            address = binding.etAddress.text.toString().trim(),
            password = binding.etPassword.text.toString().trim()
        )

        viewModel.editRider(id, request).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data
                    if (response != null && response.isSuccessful) {

                        val baseResponse = response.body()

                        // ✅ Show backend message even on success
                        Toast.makeText(this, baseResponse?.message ?: "Success", Toast.LENGTH_LONG).show()

                        if (baseResponse?.result == "success") {
                            val resultIntent = Intent()
                            resultIntent.putExtra("ACTION", "UPDATED")
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }

                    } else {
                        // ✅ Parse errorBody and show backend message even if HTTP is not successful
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
                            // ✅ Parse errorBody and show backend message
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

        val id = intent.getIntExtra("ID", 0)

        viewModel1.deleteRider(id).observe(this) { response ->
            AppUtil.stopLoader()
            val message = response.data?.body()?.message ?: "Shop deleted"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // Close activity if success
            if (response.data?.body()?.result == "success")
            {


                if (response.data.body()?.result == "success") {
                    val resultIntent = Intent()
                    resultIntent.putExtra("ACTION", "DELETED")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
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
