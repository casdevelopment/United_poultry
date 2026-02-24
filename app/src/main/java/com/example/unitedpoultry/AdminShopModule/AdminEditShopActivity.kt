package com.example.unitedpoultry.AdminShopModule

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
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteShopViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.UpdateShopViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditShopBinding
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AdminEditShopActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditShopBinding
    private val viewModel: UpdateShopViewModel by viewModel()
    private val viewModel1: DeleteShopViewModel by viewModel()

    private var selectedImageFile: File? = null
    private var shopId: Int = 0

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
        binding = ActivityAdminEditShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)

        shopId = intent.getIntExtra("SHOP_ID", 0)
        if (shopId == 0) {
            Toast.makeText(this, "Invalid shop id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupClicks()
        prefillData()
    }

    // ================= PREFILL =================
    private fun prefillData() {
        binding.etShopName.setText(intent.getStringExtra("SHOP_NAME"))
        binding.etContactName.setText(intent.getStringExtra("CONTACT"))
        binding.etPhoneNumber.setText(intent.getStringExtra("PHONE"))
        binding.etAddress.setText(intent.getStringExtra("SHOP_ADDRESS"))
        binding.etDiscount.setText(intent.getStringExtra("DISCOUNT"))

        val areaId = intent.getIntExtra("AREA_ID", 0)
        binding.etArea.setText(areaId.toString())

        binding.toggleStatus.isChecked = intent.getBooleanExtra("IS_ACTIVE", true)

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

        binding.btnCancel.setOnClickListener { finish() }
        binding.backArrow.setOnClickListener { finish() }

        binding.imageContainer.setOnClickListener { showImagePicker() }

        binding.btnUpdate.setOnClickListener {
            if (validateInputs()) callUpdateShopApi()
        }

        binding.btnDeleteShops.setOnClickListener {
            showDeleteConfirmation()
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
//        val hasImage = selectedImageFile != null || binding.imgShop.drawable != null
//        if (!hasImage) {
//            binding.etImageError.visibility = View.VISIBLE
//            binding.etImageError.text = "Shop image required"
//            valid = false
//        }
//
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

    // ================= UPDATE API =================
    private fun callUpdateShopApi() {
        val isActive = if (binding.toggleStatus.isChecked) "1" else "0"

        val name = binding.etShopName.text.toString().toRequestBody()
        val contact = binding.etContactName.text.toString().toRequestBody()
        val phone = binding.etPhoneNumber.text.toString().toRequestBody()
        val address = binding.etAddress.text.toString().toRequestBody()
        val discountValue =
            if (binding.etDiscount.text.toString().trim().isEmpty()) "0"
            else binding.etDiscount.text.toString().trim()

        val discount = discountValue.toRequestBody()
        val active = isActive.toRequestBody()

        val imagePart = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val type = "PUT"
        val method = type.toRequestBody()

//        AppUtil.startLoader(this)
//        viewModel.updateShop(shopId, name, contact, phone, address, discount, active, imagePart,method).observe(this) { response ->
//            AppUtil.stopLoader()
//            val message = response.data?.body()?.message ?: "Updated successfully"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//
//            if (response.data?.body()?.result == "success") {
//                // ✅ Return "UPDATED" to DetailsActivity
//                val resultIntent = Intent()
//                resultIntent.putExtra("ACTION", "UPDATED")
//                setResult(Activity.RESULT_OK, resultIntent)
//                finish()
//            }
//
//
//        }

        AppUtil.startLoader(this)
        viewModel.updateShop(
            shopId, name, contact, phone, address, discount, active, imagePart,method).observe(this) { apiResponse ->
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
                                    val resultIntent = Intent()
                                    resultIntent.putExtra("ACTION", "UPDATED")
                                    setResult(Activity.RESULT_OK, resultIntent)
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


    // ================= DELETE =================
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Shop")
            .setMessage("Are you sure you want to delete this shop?")
            .setPositiveButton("Yes") { _, _ -> callDeleteShopApi() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun callDeleteShopApi() {
        AppUtil.startLoader(this)

        viewModel1.deleteShop(shopId).observe(this) { response ->
            AppUtil.stopLoader()

            // Only show API message if available
            val apiMessage = response.data?.body()?.message
            if (!apiMessage.isNullOrEmpty()) {
                Toast.makeText(this, apiMessage, Toast.LENGTH_SHORT).show()
            }

            // Close activity if success
            if (response.data?.body()?.result == "success") {
                // ✅ Return "DELETED" to DetailsActivity
                val resultIntent = Intent()
                resultIntent.putExtra("ACTION", "DELETED")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

}
