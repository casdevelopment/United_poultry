package com.example.unitedpoultry.AdminShopModule

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.unitedpoultry.AdminShopModule.viewmodel.AddShopViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminAddNewShopBinding
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import com.example.unitedpoultry.network.retrofit.BaseResponse


class AdminAddNewShopActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminAddNewShopBinding
    private val viewModel: AddShopViewModel by viewModel()

    private var selectedImageFile: File? = null

    // ------------------ GALLERY ------------------
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleGalleryImage(it) }
    }

    // ------------------ CAMERA ------------------
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { handleCameraImage(it) } ?: Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddNewShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)
        setupClicks()
        resetImageViews()
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener { finish() }

        binding.imageContainer.setOnClickListener { showImagePickerDialog() }

        binding.btnSave.setOnClickListener {
            if (validateInputs()) callAddShopApi()
        }
    }

    // ================= IMAGE PICKER =================
    private fun showImagePickerDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpenCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
                    .setTitle("Camera Permission Required")
                    .setMessage("Camera access is required to take shop images.")
                    .setPositiveButton("Grant") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null) // TakePicturePreview automatically opens camera and returns bitmap
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleCameraImage(bitmap: Bitmap) {
        // Show immediately
        binding.imgShop.visibility = View.VISIBLE
        binding.imgCamera.visibility = View.GONE
        binding.imgShop.setImageBitmap(bitmap)

        // Save bitmap to file for API upload
        selectedImageFile = File(getExternalFilesDir(null), "shop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun handleGalleryImage(uri: Uri) {
        selectedImageFile = getFileFromUri(uri)
        binding.imgShop.visibility = View.VISIBLE
        binding.imgCamera.visibility = View.GONE

        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.imgShop)
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        inputStream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        return tempFile
    }

    private fun resetImageViews() {
        selectedImageFile = null
        binding.imgShop.visibility = View.GONE
        binding.imgCamera.visibility = View.VISIBLE
    }

    // ================= VALIDATION =================
    private fun validateInputs(): Boolean {

        var valid = true

        binding.etShopNameError.visibility = View.GONE
        binding.etContactNameError.visibility = View.GONE
        binding.etPhoneNumberError.visibility = View.GONE
        binding.etAreaError.visibility = View.GONE
        binding.etAddressError.visibility = View.GONE
        binding.etDiscountError.visibility = View.GONE
        binding.etImageError.visibility = View.GONE

        // Shop name

        val shopName = binding.etShopName.text.toString().trim()

        if (shopName.isEmpty()) {
            binding.etShopNameError.visibility = View.VISIBLE
            binding.etShopNameError.text = "Shop name required"
            valid = false
        }
//        else if (!shopName.matches(Regex(".*[a-zA-Z].*"))) {
//            binding.etShopNameError.visibility = View.VISIBLE
//            binding.etShopNameError.text = "Enter valid Shop name"
//            valid = false
//        }
//
//        // Contact person name (alphabets only)
//        val contactName = binding.etContactName.text.toString().trim()
//        if (contactName.isEmpty()) {
//            binding.etContactNameError.visibility = View.VISIBLE
//            binding.etContactNameError.text = "Contact person required"
//            valid = false
//        } else if (!contactName.matches(Regex("^[a-zA-Z ]+$"))) {
//            binding.etContactNameError.visibility = View.VISIBLE
//            binding.etContactNameError.text = "Enter valid name"
//            valid = false
//        }
//
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
//
//        if (selectedImageFile == null) {
//            binding.etImageError.visibility = View.VISIBLE
//            binding.etImageError.text = "Shop image required"
//            valid = false
//        }
//
//        val address = binding.etAddress.text.toString().trim()
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

        val discountText = binding.etDiscount.text.toString().trim()

        if (discountText.isNotEmpty() && !discountText.matches(Regex("^\\d+(\\.\\d+)?$"))) {
            binding.etDiscountError.visibility = View.VISIBLE
            binding.etDiscountError.text = "Enter valid discount"
            valid = false
        }



        return valid
    }


    private fun TextView.show(msg: String) {
        visibility = View.VISIBLE
        text = msg
    }

    private fun callAddShopApi() {
        val isActive = if (binding.toggleStatus.isChecked) "1" else "0"
        val areaIdString = intent.getStringExtra("AREA_ID") ?: "0"

        val name = binding.etShopName.text.toString().trim().toRequestBody()
        val contactPerson = binding.etContactName.text.toString().trim().toRequestBody()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim().toRequestBody()
        val areaId = areaIdString.toRequestBody()
        val address = binding.etAddress.text.toString().trim().toRequestBody()

        val discountValue =
            if (binding.etDiscount.text.toString().trim().isEmpty()) "0"
            else binding.etDiscount.text.toString().trim()

        val discount = discountValue.toRequestBody()

        val active = isActive.toRequestBody()

//        val imagePart = selectedImageFile?.let {
//            MultipartBody.Part.createFormData("image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
//        } ?: run {
//            Toast.makeText(this, "Image file not selected", Toast.LENGTH_SHORT).show()
//            return
//        }

        val imagePart: MultipartBody.Part? = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        AppUtil.startLoader(this)
        viewModel.addShop(name, contactPerson, phoneNumber, areaId, address, discount, active, imagePart)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    com.example.unitedpoultry.network.Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null) {
                            if (retrofitResponse.isSuccessful) {
                                val baseResponse = retrofitResponse.body()
                                val message = baseResponse?.message ?: "Shop added"
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                                // If API says success, finish activity
                                if (baseResponse?.result == "success") {

                                    finish()
                                }
                            } else {
                                // HTTP error (like 401, 422, 500)
                                val errorMessage = try {
                                    val errorBody = retrofitResponse.errorBody()?.string()
                                    if (!errorBody.isNullOrEmpty()) {
                                        val baseResponse =
                                            Gson().fromJson(errorBody, BaseResponse::class.java)
                                        baseResponse.message ?: "Something went wrong"
                                    } else {
                                        "Something went wrong"
                                    }
                                } catch (e: Exception) {
                                    "Something went wrong"
                                }
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show()
                        }
                    }

                    com.example.unitedpoultry.network.Status.ERROR -> {
                        Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                    }

                    com.example.unitedpoultry.network.Status.LOADING -> {
                        /* Loader already handled */
                    }
                }
            }
    }


    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }
}
