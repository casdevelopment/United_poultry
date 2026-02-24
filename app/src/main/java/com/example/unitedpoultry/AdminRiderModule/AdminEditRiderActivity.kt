package com.example.unitedpoultry.AdminRiderModule


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.EditRiderViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteRiderViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditRiderBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AdminEditRiderActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditRiderBinding
    private val viewModel: EditRiderViewModel by viewModel()
    private val viewModel1: DeleteRiderViewModel by viewModel()

    private var selectedImageFile: File? = null

    // ================= CAMERA =================
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { handleCameraImage(it) }
        }

    // ================= GALLERY =================
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleGalleryImage(it) }
        }




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
//        val username = intent.getStringExtra("USERNAME") ?: ""
        val password = intent.getStringExtra("PASSWORD") ?: ""
        val isActive = intent.getBooleanExtra("AREA_Status", true)



        // Set values to views
        binding.etName.setText(name)
        binding.etCnic.setText(cnic)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phone)
        binding.etAddress.setText(address)
        //binding.etUserName.setText(username)
        binding.etPassword.setText(password)
        binding.toggleStatus.isChecked = isActive

        val imageUrl = intent.getStringExtra("IMAGE")
        if (!imageUrl.isNullOrEmpty()) {
            binding.imgShop.visibility = View.VISIBLE
            binding.imgCamera.visibility = View.GONE

            // Use full URL to show existing image
            val fullImageUrl = AppConstants.ImageURL + imageUrl
            Glide.with(this)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(binding.imgShop.drawable)
                .into(binding.imgShop)
        }

    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.imageContainer.setOnClickListener { showImagePicker() }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                callEditAreaApi()
            }
        }

        binding.btnDeleteShops.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showImagePicker() {
        AlertDialog.Builder(this)
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                if (which == 0) openCamera() else openGallery()
            }.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleCameraImage(bitmap: Bitmap) {
        // Save bitmap to file
        selectedImageFile = File(cacheDir, "shop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        // Display using Glide for proper scaling
        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.imgShop)

        binding.imgShop.visibility = View.VISIBLE
        binding.imgCamera.visibility = View.GONE
    }

    private fun handleGalleryImage(uri: Uri) {
        // Save URI to temp file for upload
        selectedImageFile = File(cacheDir, "shop_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            selectedImageFile!!.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Display selected image
        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.imgShop)

        binding.imgShop.visibility = View.VISIBLE
        binding.imgCamera.visibility = View.GONE
    }



    private fun validateInputs(): Boolean {
        var valid = true

        binding.etNameError.visibility = View.GONE
        binding.etCnicError.visibility = View.GONE
        binding.etEmailError.visibility = View.GONE
        binding.etPhoneNumberError.visibility = View.GONE
        binding.etAddressError.visibility = View.GONE
       // binding.etUserNameError.visibility = View.GONE
        binding.etImageError.visibility = View.GONE




        val Name = binding.etName.text.toString().trim()
        if (Name.isEmpty()) {
            binding.etNameError.visibility = View.VISIBLE
            binding.etNameError.text = "Name required"
            valid = false
        }
//        else if (!Name.matches(Regex("^[a-zA-Z ]+$"))) {
//            binding.etNameError.visibility = View.VISIBLE
//            binding.etNameError.text = "Enter valid name"
//            valid = false
//        }
//
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
//
//        val email = binding.etEmail.text.toString().trim()
//
//        if (email.isEmpty()) {
//            binding.etEmailError.visibility = View.VISIBLE
//            binding.etEmailError.text = "Email required"
//            valid = false
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.etEmailError.visibility = View.VISIBLE
//            binding.etEmailError.text = "Enter valid email address"
//            valid = false
//        }
//
//        val phone = binding.etPhoneNumber.text.toString().trim()
//
//        if (phone.isEmpty()) {
//            binding.etPhoneNumberError.visibility = View.VISIBLE
//            binding.etPhoneNumberError.text = "Phone number required"
//            valid = false
//        } else if (!phone.matches(Regex("^\\d{11}$"))){
//            binding.etPhoneNumberError.visibility = View.VISIBLE
//            binding.etPhoneNumberError.text = "Enter valid phone number"
//            valid = false
//        }
//
//        val address = binding.etAddress.text.toString().trim()
//
//        if (address.isEmpty()) {
//            binding.etAddressError.visibility = View.VISIBLE
//            binding.etAddressError.text = "Address required"
//            valid = false
//        }else if (!address.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etAddressError.visibility = View.VISIBLE
//            binding.etAddressError.text = "Enter valid address"
//            valid = false
//        }
//
//        val username = binding.etUserName.text.toString().trim()
//
//        if (username.isEmpty()) {
//            binding.etUserNameError.visibility = View.VISIBLE
//            binding.etUserNameError.text = "User Name required"
//            valid = false
//        } else if (!username.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etUserNameError.visibility = View.VISIBLE
//            binding.etUserNameError.text = "Enter valid username"
//            valid = false
//        }
//
//
        val password = binding.etPassword.text.toString().trim()
        if (password.isNotEmpty() && password.length < 8) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.text = "Password must be at least 8 characters"
            valid = false
        }
//
//        val hasImage = selectedImageFile != null || binding.imgShop.drawable != null
//        if (!hasImage) {
//            binding.etImageError.visibility = View.VISIBLE
//            binding.etImageError.text = "Shop image required"
//            valid = false
//        }
//

        return valid
    }


    private fun callEditAreaApi() {

//        val isActive = binding.toggleStatus.isChecked

        val id = intent.getIntExtra("ID", 0)

        val passwordd = binding.etPassword.text.toString().trim()
//        val request = RiderEditRequestModel(
//            name = binding.etName.text.toString().trim(),
//            email = binding.etEmail.text.toString().trim(),
//            username = binding.etUserName.text.toString().trim(),
//            phone_number = binding.etPhoneNumber.text.toString().trim(),
//            cnic = binding.etCnic.text.toString().trim(),
//            address = binding.etAddress.text.toString().trim(),
//            password = if (password.isNotEmpty() && password.length >= 8) password else null,
//            is_active = isActive
//        )

       // val isActive = if (binding.toggleStatus.isChecked) "1" else "0"

        val isActive = if (binding.toggleStatus.isChecked) "1" else "0"
        val isActiveBody = isActive.toRequestBody("text/plain".toMediaTypeOrNull())


        val name = binding.etName.text.toString().toRequestBody()
        val email = binding.etEmail.text.toString().toRequestBody()
        //val username = binding.etUserName.text.toString().toRequestBody()
        val phone_number = binding.etPhoneNumber.text.toString().toRequestBody()
        val cnic = binding.etCnic.text.toString().toRequestBody()
        val address = binding.etAddress.text.toString().toRequestBody()

        val passwordBody: RequestBody? =
            if (passwordd.isNotEmpty() && passwordd.length >= 8) {
                passwordd.toRequestBody("text/plain".toMediaTypeOrNull())
            } else {
                null
            }


        val imagePart = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }



        viewModel.editRider(id, name,email,phone_number,cnic,address,passwordBody,isActiveBody,imagePart).observe(this) { apiResponse ->

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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Shop")
            .setMessage("Are you sure you want to delete this Rider?")
            .setPositiveButton("Yes") { _, _ -> callDeleteRiderApi() }
            .setNegativeButton("No", null)
            .show()
    }

//    private fun callDeleteShopApi() {
//        AppUtil.startLoader(this)
//
//        val id = intent.getIntExtra("ID", 0)
//
//        viewModel1.deleteRider(id).observe(this) { response ->
//            AppUtil.stopLoader()
//            val message = response.data?.body()?.message ?: "Shop deleted"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//            // Close activity if success
//            if (response.data?.body()?.result == "success")
//            {
//
//
//                if (response.data.body()?.result == "success") {
//                    val resultIntent = Intent()
//                    resultIntent.putExtra("ACTION", "DELETED")
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()
//                }
//
//
//            }
//        }
//    }

    private fun callDeleteRiderApi() {
        AppUtil.startLoader(this)

        val id = intent.getIntExtra("ID", 0)

        viewModel1.deleteRider(id).observe(this) { response ->
            AppUtil.stopLoader()

            // Only show API message if available
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
