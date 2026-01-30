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
import com.example.unitedpoultry.databinding.ActivityAdminEditProfileBinding
import com.example.unitedpoultry.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AdminEditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditProfileBinding
    private val viewModel: UpdateAdminProfileViewModel by viewModel()
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
        binding = ActivityAdminEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(true, android.R.color.white)


        prefillData()
        setupClicks()
    }

    // ================= PREFILL =================
    private fun prefillData() {
        val name = intent.getStringExtra("NAME") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val phone = intent.getStringExtra("PHONE_NUMBER") ?: ""
        val username = intent.getStringExtra("USERNAME") ?: ""
        val address = intent.getStringExtra("ADDRESS") ?: ""
        //val imageUrl = intent.getStringExtra("IMAGE_URL") ?: ""

        binding.etFullName.setText(name)
        binding.etEmail.setText(email)
        binding.etPhoneNumber.setText(phone)
        binding.etUserName.setText(username)
        binding.etAddress.setText(address)

        // Set initials if no image
//        if (imageUrl.isNotEmpty()) {
//            binding.ivImage.visibility = View.VISIBLE
//            binding.tvInitials.visibility = View.GONE
//
//            Glide.with(this)
//                .load(imageUrl)
//                .centerCrop()
//                .into(binding.ivImage)
//        } else {
//            binding.ivImage.visibility = View.GONE
//            binding.tvInitials.visibility = View.VISIBLE
            binding.tvInitials.text = intent.getStringExtra("INITIALS") ?: ""
       // }
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
        if (binding.etEmail.text.toString().trim().isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.setText("Email required")
            valid = false
        }
        if (binding.etPhoneNumber.text.toString().trim().length < 10) {
            binding.etPhoneNumberError.visibility = View.VISIBLE
            binding.etPhoneNumberError.setText("valid phone number required")
            valid = false
        }
        if (binding.etUserName.text.toString().trim().isEmpty()) {
            binding.etUserNameError.visibility = View.VISIBLE
            binding.etUserNameError.setText("User Name required")
            valid = false
        }
        if (binding.etAddress.text.toString().trim().isEmpty()) {
            binding.etAddressError.visibility = View.VISIBLE
            binding.etAddressError.setText("Address required")
            valid = false
        }

        if (binding.etBusinessName.text.toString().trim().isEmpty()) {
            binding.etBusinessNameError.visibility = View.VISIBLE
            binding.etBusinessNameError.setText("BusinessName required")
            valid = false
        }
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
        viewModel.updateProfile(
             name, email, phone, username, business_name,address, imagePart
        ).observe(this) { response ->
            AppUtil.stopLoader()
            val message = response.data?.body()?.message ?: "Profile updated"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            if (response.data?.body()?.result == "success") {
                setResult(Activity.RESULT_OK)
                finish()
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
