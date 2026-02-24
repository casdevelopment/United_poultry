package com.example.unitedpoultry.AdminSettingModule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.unitedpoultry.AdminSettingModule.viewmodel.UpdateAdminProfileViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.UpdateShopViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.databinding.ActivityAdminEditProfileBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AdminEditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditProfileBinding
    private val viewModel: UpdateAdminProfileViewModel by viewModel()
    private var selectedImageFile: File? = null
    private val sessionManager: SessionManager by inject()


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
        binding = ActivityAdminEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)


        prefillData()
        setupClicks()
    }

    // ================= PREFILL =================
    private fun prefillData() {
//        val name = intent.getStringExtra("NAME") ?: ""
//        val email = intent.getStringExtra("EMAIL") ?: ""
//        val phone = intent.getStringExtra("PHONE_NUMBER") ?: ""
//        val username = intent.getStringExtra("USERNAME") ?: ""
//        val address = intent.getStringExtra("ADDRESS") ?: ""
//        val imageUrl = intent.getStringExtra("IMAGE_URL") ?: ""



        val name = userData?.name ?: "User Name"
        val email = userData?.email ?: "Email"
        val phone = userData?.phone_number ?: "Phone Number"
        val username = userData?.username ?: "UserName"
        val address = userData?.address ?: "Address"

        binding.etFullName.setText(name)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phone)
        binding.etUserName.setText(username)
        binding.etAddress.setText(address)


        val imageUrl = userData?.image
        if (!imageUrl.isNullOrEmpty()) {
            binding.imageContainer.visibility = View.VISIBLE
            //  binding.imgCamera.visibility = View.GONE

            // Use full URL to show existing image
            val fullImageUrl = AppConstants.ImageURL + imageUrl
            Glide.with(this)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(binding.ivImage.drawable)
                .into(binding.ivImage)
        } else {
            binding.imageContainer.visibility = View.GONE
            binding.tvInitials.visibility = View.VISIBLE
            binding.tvInitials.text = getInitials(userData?.name)
        }

    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return "U" // Default initial
        val words = name.trim().split(" ")
        return when {
            words.size >= 2 -> "${words[0][0]}${words[1][0]}".uppercase()
            words.isNotEmpty() -> "${words[0][0]}".uppercase()
            else -> "U"
        }
    }


    private fun setupClicks() {
        // Back
        binding.backArrow.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener { finish() }

        // Tick -> select image
        binding.ivTick.setOnClickListener { showImagePicker() }

        // Save changes
        binding.btnSave.setOnClickListener {
            if (validateInputs()) callUpdateProfileApi()
        }
    }

    // ================= IMAGE PICKER =================
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
        selectedImageFile = File(cacheDir, "profile_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

        Glide.with(this).load(selectedImageFile).centerCrop().into(binding.ivImage)
        binding.ivImage.visibility = View.VISIBLE
        binding.tvInitials.visibility = View.GONE
    }

    private fun handleGalleryImage(uri: Uri) {
        selectedImageFile = File(cacheDir, "profile_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            selectedImageFile!!.outputStream().use { output -> input.copyTo(output) }
        }

        Glide.with(this).load(selectedImageFile).centerCrop().into(binding.ivImage)
        binding.ivImage.visibility = View.VISIBLE
        binding.tvInitials.visibility = View.GONE
    }

    // ================= VALIDATION =================
    private fun validateInputs(): Boolean {
        var valid = true
        if (binding.etFullName.text.toString().trim().isEmpty()) {
            binding.etFullName.visibility = View.VISIBLE
            binding.etFullName.setText("Name required")
            valid = false
        }
//        if (binding.etEmail.text.toString().trim().isEmpty()) {
//            binding.etEmailError.visibility = View.VISIBLE
//            binding.etEmailError.setText("Email required")
//            valid = false
//        }
//        if (binding.etPhoneNumber.text.toString().trim().length < 10) {
//            binding.etPhoneNumberError.visibility = View.VISIBLE
//            binding.etPhoneNumberError.setText("valid phone number required")
//            valid = false
//        }
//        if (binding.etUserName.text.toString().trim().isEmpty()) {
//            binding.etUserNameError.visibility = View.VISIBLE
//            binding.etUserNameError.setText("User Name required")
//            valid = false
//        }
//        if (binding.etAddress.text.toString().trim().isEmpty()) {
//            binding.etAddressError.visibility = View.VISIBLE
//            binding.etAddressError.setText("Address required")
//            valid = false
//        }
//
//        if (binding.etBusinessName.text.toString().trim().isEmpty()) {
//            binding.etBusinessNameError.visibility = View.VISIBLE
//            binding.etBusinessNameError.setText("BusinessName required")
//            valid = false
//        }
        return valid
    }

    // ================= UPDATE API =================
    private fun callUpdateProfileApi() {
        val name = binding.etFullName.text.toString().toRequestBody()
        val email = binding.etEmail.text.toString().toRequestBody()
        val phone = binding.etPhoneNumber.text.toString().toRequestBody()
        val username = binding.etUserName.text.toString().toRequestBody()
        val business_name = binding.etBusinessName.text.toString().toRequestBody()
        val address = binding.etAddress.text.toString().toRequestBody()


        val imagePart = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        AppUtil.startLoader(this)

        // Replace with your ViewModel API call
//        viewModel.updateProfile(name, email, phone, username, business_name,address, imagePart).observe(this) { response ->
//            AppUtil.stopLoader()
//            val message = response.data?.body()?.message ?: "Profile updated"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//            if (response.data?.body()?.result == "success") {
//                setResult(Activity.RESULT_OK)
//                finish()
//            }
//        }


        viewModel.updateProfile(name, email, phone, username, business_name,address, imagePart).observe(this) { apiResponse ->

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

//                        if (baseResponse?.result == "success") {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }

                        val updatedUser = baseResponse?.data

                        if (baseResponse?.result == "success" && updatedUser != null) {

                            val oldUser = AppConstants.userData

                            val mergedUser = oldUser?.copy(
                                name = updatedUser.name,
                                email = updatedUser.email,
                                phone_number = updatedUser.phone_number,
                                username = updatedUser.username,
                                address = updatedUser.address,
                                image = updatedUser.image,
                                updated_at = updatedUser.updated_at
                            )

                            mergedUser?.let {
                                sessionManager.userInfo(Gson().toJson(it))
                                AppConstants.userData = it
                            }

                            setResult(Activity.RESULT_OK)
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

    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())

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
}
