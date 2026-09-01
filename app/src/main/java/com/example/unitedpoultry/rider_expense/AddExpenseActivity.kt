package com.example.unitedpoultry.rider_expense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAddExpenseBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_expense.Model.AddExpenseRequest
import com.example.unitedpoultry.rider_expense.Model.ExpenseHead
import com.example.unitedpoultry.rider_expense.Model.ExpenseHeadData
import com.example.unitedpoultry.rider_expense.ViewModel.ExpenseViewModel
import com.example.unitedpoultry.rider_expense.adapter.ExpenseAdapter
import com.example.unitedpoultry.util.AppUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : BaseActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: ExpenseViewModel by viewModel()

    private val expenseAdapter by lazy {
        ExpenseAdapter(emptyList()) { newTotal ->
            updateTotalUI(newTotal)
        }
    }

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
        selectButton(binding.cashLayout)
        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupRecycler() {
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = expenseAdapter
        updateTotalUI(0.0)
    }

    private fun updateTotalUI(total: Double) {
        val formattedTotal = if (total % 1.0 == 0.0) {
            total.toInt().toString()
        } else {
            String.format(Locale.getDefault(), "%.2f", total)
        }

        binding.etAmount.setText("Rs. $formattedTotal")
    }

    private fun loadProducts() {
        viewModel.getExpenses().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body()
                        if (baseResponse?.result == "success" && baseResponse.data != null) {
                            expenseAdapter.updateData(baseResponse.data.heads)
                        } else {
                            Toast.makeText(this, baseResponse?.message ?: "Failed to fetch expense heads", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                if (paymentType == "cash") {
                    addExpenseByCash()
                } else {
                    addExpenseByCheque()
                }
            }
        }
    }

    private fun selectButton(selected: LinearLayout) {

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val whiteColor = ContextCompat.getColor(this, R.color.white)


        if (selected == binding.cashLayout) {
            paymentType = "cash"



            binding.cashLayout.backgroundTintList = ColorStateList.valueOf(primaryColor)
            binding.addExpenseLayout.backgroundTintList = ColorStateList.valueOf(whiteColor)

            binding.cashText.setTextColor(whiteColor)
            binding.cashIcon.setColorFilter(whiteColor)

            binding.onlineText.setTextColor(primaryColor)
            binding.onlineIcon.setColorFilter(primaryColor)

            binding.uploadTitle.visibility = View.GONE
            binding.imageContainer.visibility = View.GONE
        } else {
            paymentType = "online_cheque"


            binding.cashLayout.backgroundTintList = ColorStateList.valueOf(whiteColor)
            binding.addExpenseLayout.backgroundTintList = ColorStateList.valueOf(primaryColor)

            binding.cashText.setTextColor(primaryColor)
            binding.cashIcon.setColorFilter(primaryColor)

            binding.onlineText.setTextColor(whiteColor)
            binding.onlineIcon.setColorFilter(whiteColor)

            binding.uploadTitle.visibility = View.VISIBLE
            binding.imageContainer.visibility = View.VISIBLE
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etAmountError.visibility = View.GONE
        binding.imageError.visibility = View.GONE

        val totalAmount = expenseAdapter.getTotalAmount()

        if (totalAmount <= 0) {
            binding.etAmountError.visibility = View.VISIBLE
            binding.etAmountError.text = "At least one expense amount required"
            valid = false
        }

        if (selectedImageFile == null && paymentType == "online_cheque") {
            binding.imageError.visibility = View.VISIBLE
            binding.imageError.text = "Image required"
            valid = false
        }

        return valid
    }

    private fun createExpenseRequestBody(): AddExpenseRequest {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val note = binding.etNote1.text.toString().trim()

        return AddExpenseRequest(
            expenses = expenseAdapter.getEnteredExpenses(),
            total_amount = expenseAdapter.getTotalAmount(),
            payment_type = paymentType,
            expense_date = todayDate,
            note = note
        )
    }

    private fun addExpenseByCash() {
        val expenseList = expenseAdapter.getEnteredExpenses()
        val totalAmount = expenseAdapter.getTotalAmount()
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val noteText = binding.etNote1.text.toString().trim()

        val expensesJsonString = Gson().toJson(expenseList)

        val expensesPart = expensesJsonString.toRequestBody("application/json".toMediaTypeOrNull())

        val totalAmountPart = totalAmount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val paymentTypePart = paymentType.toRequestBody("text/plain".toMediaTypeOrNull())
        val expenseDatePart = todayDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val notePart = noteText.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

        AppUtil.startLoader(this)
        viewModel.addExpenseByCash(expensesPart, totalAmountPart, paymentTypePart, expenseDatePart, notePart)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null && retrofitResponse.isSuccessful) {
                            val baseResponse = retrofitResponse.body()
                            Toast.makeText(this, baseResponse?.message ?: "Expense added", Toast.LENGTH_SHORT).show()

                            if (baseResponse?.result == "success") {
                                val jsonData = Gson().toJson(baseResponse.data)
                                val intent = Intent(this, ExpenseSuccessActivity::class.java).apply {
                                    putExtra("expense_data_json", jsonData)
                                }
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, apiResponse.message ?: "Network connection error", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> AppUtil.startLoader(this)
                }
            }
    }








    private fun addExpenseByCheque() {
        val expenseList = expenseAdapter.getEnteredExpenses()
        val totalAmount = expenseAdapter.getTotalAmount()
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val noteText = binding.etNote1.text.toString().trim()

        val expensesJsonString = Gson().toJson(expenseList)

        val expensesPart = expensesJsonString.toRequestBody("application/json".toMediaTypeOrNull())

        val totalAmountPart = totalAmount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val paymentTypePart = paymentType.toRequestBody("text/plain".toMediaTypeOrNull())
        val expenseDatePart = todayDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val notePart = noteText.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

        val paymentRecordPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "payment_record",
            selectedImageFile!!.name,
            selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val paymentNotePart = binding.etNote2.text.toString().trim().toRequestBody()

        AppUtil.startLoader(this)
        viewModel.addExpenseByCheque(expensesPart, totalAmountPart, paymentTypePart, expenseDatePart, notePart, paymentRecordPart, paymentNotePart)
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
                                        val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
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

    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())

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
        binding.selectedImage.visibility = View.VISIBLE
        binding.txtPlaceholder.visibility = View.GONE
        binding.selectedImage.setImageBitmap(bitmap)

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