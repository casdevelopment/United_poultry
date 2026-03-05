package com.example.unitedpoultry.Collection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.ViewModel.CollectionViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityCollectionformBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class CollectionformActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionformBinding
    private val viewModel: CollectionViewModel by viewModel()

    private var shopId: Int = 0

    private var name: String = ""
    private var address: String = ""

    private var borrowed: Int = 0

    private var paymentType = "cash"

    private var selectedImageFile: File? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleGalleryImage(it) }
    }

    // ------------------ CAMERA ------------------
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { handleCameraImage(it) } ?: Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        shopId = intent.getIntExtra("SHOP_ID", 0)

        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"

        borrowed = intent.getIntExtra("BORROWED", 0)

        showData()

        setupCollectionWatcher()

        setupClicks()
        resetImageViews()

        selectButton(binding.cashLayout)


    }

    private fun showData() {

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)


        binding.tvBalance.text = "Rs ${borrowed}"


        binding.tvAfterBalance.text = "Rs ${borrowed}"

    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
    }

    private fun setupClicks() {

        binding.cashLayout.setOnClickListener {
            selectButton(binding.cashLayout)
        }

        binding.addExpenseLayout.setOnClickListener {
            selectButton(binding.addExpenseLayout)
        }

        binding.imageContainer.setOnClickListener { showImagePickerDialog() }

        binding.btnConfirmCollection.setOnClickListener {
            val isAmountValid = validateCollectionInputs()

            // For cash, only amount validation
            if (paymentType == "cash") {
                if (isAmountValid) addCollectionByCash()
            }
            // For online_cheque, both validations
            else {
                val isImageValid = validateImage()
                if (isAmountValid && isImageValid) addCollectionByCheque()
            }
        }

    }


    private fun selectButton(selected: LinearLayout) {


        if (selected == binding.cashLayout) {

            paymentType = "cash"

            // For a LinearLayout
            val primaryColor = ContextCompat.getColor(this, R.color.primary)
            val whiteColor = ContextCompat.getColor(this, R.color.white)

            binding.cashLayout.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.addExpenseLayout.backgroundTintList = ColorStateList.valueOf(whiteColor)


            binding.cashText.setTextColor(resources.getColor(R.color.white))
            binding.cashIcon.setColorFilter(resources.getColor(R.color.white))

            binding.onlineText.setTextColor(resources.getColor(R.color.primary))
            binding.onlineIcon.setColorFilter(resources.getColor(R.color.primary))

            binding.uploadTitle.visibility  = View.GONE
            binding.imageContainer.visibility  = View.GONE
            binding.card.visibility  = View.GONE

        } else {

            paymentType = "online_cheque"

            val primaryColor = ContextCompat.getColor(this, R.color.white)
            val whiteColor = ContextCompat.getColor(this, R.color.primary)

            binding.cashLayout.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.addExpenseLayout.backgroundTintList = ColorStateList.valueOf(whiteColor)


            binding.cashText.setTextColor(resources.getColor(R.color.primary))
            binding.cashIcon.setColorFilter(resources.getColor(R.color.primary))

            binding.onlineText.setTextColor(resources.getColor(R.color.white))
            binding.onlineIcon.setColorFilter(resources.getColor(R.color.white))

            binding.uploadTitle.visibility  = View.VISIBLE
            binding.imageContainer.visibility  = View.VISIBLE
            binding.card.visibility  = View.VISIBLE
        }

    }


    private fun setupCollectionWatcher() {
        binding.etCollectionAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateCollectionInputs() // Live validation while typing
            }
        })
    }


    private fun validateCollectionInputs(): Boolean {
        var valid = true

        // Reset errors
        binding.etCollectionAmountError.visibility = View.GONE
        binding.imageError.visibility = View.GONE

        // Get borrowed amount
        val borrowedAmount = borrowed  // use class variable
        if (borrowedAmount == 0) {
            binding.etCollectionAmountError.visibility = View.VISIBLE
            binding.etCollectionAmountError.text = "Cannot add collection. Borrowed amount is zero."
            binding.tvAfterBalance.text = "Rs 0"
            binding.btnConfirmCollection.isEnabled = false
            return false
        }

        // Get entered amount
        val enteredAmount = binding.etCollectionAmount.text.toString().toIntOrNull() ?: 0

        when {
            enteredAmount <= 0 -> {
                binding.etCollectionAmountError.visibility = View.VISIBLE
                binding.etCollectionAmountError.text = "Amount must be greater than 0"
                binding.tvAfterBalance.text = "Rs $borrowedAmount"
                valid = false
            }

            enteredAmount > borrowedAmount -> {
                binding.etCollectionAmountError.visibility = View.VISIBLE
                binding.etCollectionAmountError.text = "Amount cannot exceed borrowed"
                binding.tvAfterBalance.text = "Rs 0"
                valid = false
            }

            else -> {
                // Valid amount → update remaining
                val remaining = borrowedAmount - enteredAmount
                binding.tvAfterBalance.text = "Rs $remaining"
            }
        }


        return valid
    }

    private fun validateImage(): Boolean {
        binding.imageError.visibility = View.GONE

        if (paymentType == "online_cheque" && selectedImageFile == null) {
            binding.imageError.visibility = View.VISIBLE
            binding.imageError.text = "Image required"
            return false
        }
        return true
    }


    private fun addCollectionByCash() {

        val shop_id = shopId.toString().toRequestBody()
        val amount = binding.etCollectionAmount.text.toString().trim().toRequestBody()
        val payment_type = paymentType.toRequestBody()

        AppUtil.startLoader(this)
        viewModel.addCollectionByCash(shop_id, payment_type, amount)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null && retrofitResponse.isSuccessful) {
                            val baseResponse = retrofitResponse.body()
                            val message = baseResponse?.message ?: "Collection added"
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                            if (baseResponse?.result == "success" && baseResponse.data != null) {
                                // Convert the "data" field to JSON
                                val jsonData = Gson().toJson(baseResponse.data)

                                // Pass JSON to next activity
                                val intent = Intent(this, CollectionSuccessActivity::class.java)
                                intent.putExtra("collection_data_json", jsonData)
                                startActivity(intent)

                            }

                        } else {
                            val errorMessage = try {
                                val errorBody = retrofitResponse?.errorBody()?.string()
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
                    }

                    Status.ERROR -> {
                        Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                    }

                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                    }
                }
            }
    }


    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())


    private fun addCollectionByCheque() {


        val shop_id = shopId.toString().toRequestBody()

        val amount = binding.etCollectionAmount.text.toString().trim().toRequestBody()


        val payment_type = paymentType.toRequestBody()

        val payment_record: MultipartBody.Part = MultipartBody.Part.createFormData(
            "payment_record",
            selectedImageFile!!.name,
            selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val payment_note = binding.etPaymentNote.text.toString().trim().toRequestBody()


        AppUtil.startLoader(this)
        viewModel.addCollectionByCheque(shop_id, payment_type, amount, payment_record,payment_note)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null && retrofitResponse.isSuccessful) {
                            val baseResponse = retrofitResponse.body()
                            val message = baseResponse?.message ?: "Collection added"
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                            if (baseResponse?.result == "success" && baseResponse.data != null) {
                                // Convert the "data" field to JSON
                                val jsonData = Gson().toJson(baseResponse.data)

                                // Pass JSON to next activity
                                val intent = Intent(this, CollectionSuccessActivity::class.java)
                                intent.putExtra("collection_data_json", jsonData)
                                startActivity(intent)

                            }

                        } else {
                            val errorMessage = try {
                                val errorBody = retrofitResponse?.errorBody()?.string()
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
                    }

                    Status.ERROR -> {
                        Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                    }

                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                    }
                }
            }
    }









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
        binding.selectedImage.visibility = View.VISIBLE
        binding.txtPlaceholder.visibility = View.GONE
        binding.selectedImage.setImageBitmap(bitmap)

        // Save bitmap to file for API upload
        selectedImageFile = File(getExternalFilesDir(null), "shop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun handleGalleryImage(uri: Uri) {
        selectedImageFile = getFileFromUri(uri)
        binding.selectedImage.visibility = View.VISIBLE
        binding.txtPlaceholder.visibility = View.GONE

        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.selectedImage)
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        inputStream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        return tempFile
    }

    private fun resetImageViews() {
        selectedImageFile = null
        binding.selectedImage.visibility = View.GONE
        binding.txtPlaceholder.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }
}