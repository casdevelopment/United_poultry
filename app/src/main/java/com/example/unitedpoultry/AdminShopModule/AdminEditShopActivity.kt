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
import com.example.unitedpoultry.util.AppConstants
import com.example.unitedpoultry.util.AppUtil
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
        if (binding.etShopName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Shop name required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etContactName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Contact person required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etPhoneNumber.text.toString().trim().length < 10) {
            Toast.makeText(this, "Valid phone number required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etAddress.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Address required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etDiscount.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Discount required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // ================= UPDATE API =================
    private fun callUpdateShopApi() {
        val isActive = if (binding.toggleStatus.isChecked) "1" else "0"

        val name = binding.etShopName.text.toString().toRequestBody()
        val contact = binding.etContactName.text.toString().toRequestBody()
        val phone = binding.etPhoneNumber.text.toString().toRequestBody()
        val address = binding.etAddress.text.toString().toRequestBody()
        val discount = binding.etDiscount.text.toString().toRequestBody()
        val active = isActive.toRequestBody()

        val imagePart = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        val type = "PUT"
        val method = type.toRequestBody()

        AppUtil.startLoader(this)
        viewModel.updateShop(
            shopId, name, contact, phone, address, discount, active, imagePart,method
        ).observe(this) { response ->
            AppUtil.stopLoader()
            val message = response.data?.body()?.message ?: "Updated successfully"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            if (response.data?.body()?.result == "success")

                intent.putExtra("ACTION", "UPDATED")
                setResult(Activity.RESULT_OK, intent)
                finish()
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
            val message = response.data?.body()?.message ?: "Shop deleted"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // Close activity if success
            if (response.data?.body()?.result == "success")
            {
//                val intent = Intent(this, AdminShopListActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)

                if (response.data.body()?.result == "success") {
                    val intent = Intent()
                    intent.putExtra("ACTION", "DELETED")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }


            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
