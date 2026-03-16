package com.example.unitedpoultry.rider_expense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.CollectionSuccessActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RotateTransformation
import com.example.unitedpoultry.databinding.ActivityAddExpenseBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_expense.ViewModel.ExpenseViewModel
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: ExpenseViewModel by viewModel()

    private var paymentType = "cash"

    private var selectedImageFile: File? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleGalleryImage(it) }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { handleCameraImage(it) } ?: Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        setupClicks()
        resetImageViews()

        setupExpenseDropdown()
        selectButton(binding.cashLayout)


    }

    private fun setupClicks() {

        binding.cashLayout.setOnClickListener {
            selectButton(binding.cashLayout)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.addExpenseLayout.setOnClickListener {
            selectButton(binding.addExpenseLayout)
        }

        binding.imageContainer.setOnClickListener { showImagePickerDialog() }

        binding.btnSave.setOnClickListener {

            if (validateInputs()) {

                if(paymentType == "cash") {
                    addExpenseByCash()
                }else{
                    addExpenseByCheque()
                }
            }
        }



    }





    private fun setupExpenseDropdown() {

        val expenseList = listOf(
            "Police Charges",
            "Food Allowance",
            "Vehicle Maintenance",
            "Fuel Cost",
            "Labor Charges",
            "Other Expenses"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.item_dropdown,
            expenseList
        )

        binding.etExpenseTitle.setAdapter(adapter)

        // show dropdown when clicked
        binding.etExpenseTitle.setOnClickListener {
            binding.etExpenseTitle.showDropDown()
        }

        // optional → set default value
        binding.etExpenseTitle.setText(expenseList[0], false)
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
           // binding.card.visibility  = View.GONE

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
            //binding.card.visibility  = View.VISIBLE
        }

    }


    private fun validateInputs(): Boolean {

        var valid = true

        binding.etAmountError.visibility = View.GONE
        binding.imageError.visibility = View.GONE


        // Shop name

        val amount = binding.etAmount.text.toString().trim()

        if (amount.isEmpty()) {
            binding.etAmountError.visibility = View.VISIBLE
            binding.etAmountError.text = "Amount required"
            valid = false
        }

        if (selectedImageFile == null && paymentType == "online_cheque") {
            binding.imageError.visibility = View.VISIBLE
            binding.imageError.text = "Image required"
            valid = false
        }

        return valid
    }


    private fun addExpenseByCash() {


        val title = binding.etExpenseTitle.text.toString().trim().toRequestBody()

        val amount = binding.etAmount.text.toString().trim().toRequestBody()

        val note = binding.etNote1.text.toString().trim().toRequestBody()

        val payment_type = paymentType.toRequestBody()

        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val expense_date = todayDate.toRequestBody()


        AppUtil.startLoader(this)
        viewModel.addExpenseByCash(title, amount, note, payment_type,expense_date)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null) {
                            if (retrofitResponse.isSuccessful) {
                                val baseResponse = retrofitResponse.body()
                                val message = baseResponse?.message ?: "Expense added"
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                                if (baseResponse?.result == "success") {

                                    val jsonData = Gson().toJson(baseResponse.data)
                                    val intent = Intent(this, ExpenseSuccessActivity::class.java)
                                    intent.putExtra("expense_data_json", jsonData)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
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

                    Status.ERROR -> {
                        Toast.makeText(this,"Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                    }

                   Status.LOADING -> {
                       AppUtil.stopLoader()
                    }
                }
            }
    }


    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())


    private fun addExpenseByCheque() {


        val title = binding.etExpenseTitle.text.toString().trim().toRequestBody()

        val amount = binding.etAmount.text.toString().trim().toRequestBody()

        val note = binding.etNote1.text.toString().trim().toRequestBody()

        val payment_type = paymentType.toRequestBody()

        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val expense_date = todayDate.toRequestBody()

        val payment_record: MultipartBody.Part = MultipartBody.Part.createFormData(
            "payment_record",
            selectedImageFile!!.name,
            selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val payment_note = binding.etNote2.text.toString().trim().toRequestBody()


        AppUtil.startLoader(this)
        viewModel.addExpenseByCheque(title, amount, note, payment_type,expense_date,payment_record,payment_note)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null) {
                            if (retrofitResponse.isSuccessful) {
                                val baseResponse = retrofitResponse.body()
                                val message = baseResponse?.message ?: "Expense added"
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                                if (baseResponse?.result == "success") {

                                    val jsonData = Gson().toJson(baseResponse.data)
                                    val intent = Intent(this, ExpenseSuccessActivity::class.java)
                                    intent.putExtra("expense_data_json", jsonData)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
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

                    Status.ERROR -> {
                        Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                    }

                    Status.LOADING -> {
                        AppUtil.stopLoader()
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
        cameraLauncher.launch(null)
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

//    private fun getCameraRotation(): Float {
//        val rotation = windowManager.defaultDisplay.rotation
//        return when (rotation) {
//            Surface.ROTATION_0 -> 90f
//            Surface.ROTATION_90 -> 0f
//            Surface.ROTATION_180 -> 270f
//            Surface.ROTATION_270 -> 180f
//            else -> 90f
//        }
//    }
//
//    private fun handleCameraImage(bitmap: Bitmap) {
//        val rotatedBitmap = rotateBitmap(bitmap, getCameraRotation())
//
//        binding.selectedImage.visibility = View.VISIBLE
//        binding.txtPlaceholder.visibility = View.GONE
//        binding.selectedImage.setImageBitmap(rotatedBitmap)
//
//        // Save rotated bitmap for API upload
//        selectedImageFile = File(getExternalFilesDir(null), "shop_${System.currentTimeMillis()}.jpg")
//        FileOutputStream(selectedImageFile!!).use { out ->
//            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//        }
//    }
//
//    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
//        val matrix = android.graphics.Matrix()
//        matrix.postRotate(angle)
//        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
//    }

    private fun handleGalleryImage(uri: Uri) {
        selectedImageFile = getFileFromUri(uri)
        binding.selectedImage.visibility = View.VISIBLE
        binding.txtPlaceholder.visibility = View.GONE

        Glide.with(this)
            .load(selectedImageFile)
            .placeholder(binding.selectedImage.drawable)
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