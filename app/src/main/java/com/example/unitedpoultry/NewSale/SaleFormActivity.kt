package com.example.unitedpoultry.NewSale

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
import com.example.unitedpoultry.NewSale.Adapter.RiderProductAdapter
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class SaleFormActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleFormBinding
    private val viewModel: GetRiderProductViewModel by viewModel()
   // private val saleViewModel: RiderNewSaleViewModel by viewModel()

    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<Product>()
    private var discountPercent: Double = 0.0

    private var shopId: Int = 0
    private var areaId: Int = 0

    private var name: String = ""
    private var address: String = ""

    private var currentSubtotal: Double = 0.0
    private var currentTotalEggs: Int = 0
    private var currentNumberOfPattis: Int = 0
    private var currentDiscountAmount: Double = 0.0
    private var currentTotalAfterDiscount: Double = 0.0

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
        binding = ActivitySaleFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(isLightBackground = true, colorResId = android.R.color.white)

        discountPercent = intent.getStringExtra("DISCOUNT")?.toDoubleOrNull() ?: 0.0

        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)

        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"


        setupRecycler()
        setupClicks()
        showData()

        resetImageViews()
        selectButton(binding.cashLayout)
    }

    private fun showData() {

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)
        binding.tvDiscount.text = "Rs 0"
    }

    private fun setupClicks() {
        binding.backArrow.setOnClickListener { finish() }

        binding.btnCancel.setOnClickListener { finish() }

        binding.cashLayout.setOnClickListener {
            selectButton(binding.cashLayout)
        }

        binding.addExpenseLayout.setOnClickListener {
            selectButton(binding.addExpenseLayout)
        }

        binding.imageContainer.setOnClickListener { showImagePickerDialog() }


        binding.btnConfirmSale.setOnClickListener {

            val selectedItems = quantityMap.filter { it.value > 0 }
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            val subTotal = calculateSubtotal(selectedItems)
//            val discountAmount = subTotal * (discountPercent / 100.0)
//            val totalAmount = (subTotal - discountAmount).coerceAtLeast(0.0)

            val intent = Intent(this, SaleConfirmationActivity::class.java)

            val totalQuantity = selectedItems.values.sum()


            intent.putExtra("SHOP_NAME", name)
            intent.putExtra("ADDRESS", address)
            intent.putExtra("INITIALS", getInitials(name))

            intent.putExtra("SHOP_ID", shopId)
            intent.putExtra("AREA_ID", areaId)
            intent.putExtra("SUB_TOTAL", currentSubtotal)
            intent.putExtra("DISCOUNT", currentDiscountAmount)
            intent.putExtra("TOTAL", currentTotalAfterDiscount)
            intent.putExtra("TOTAL_QUANTITY", totalQuantity)


            // Pass product IDs & quantities
            intent.putIntegerArrayListExtra(
                "PRODUCT_IDS",
                ArrayList(selectedItems.keys)
            )

            intent.putIntegerArrayListExtra(
                "QUANTITIES",
                ArrayList(selectedItems.values)
            )

            startActivity(intent)
        }

    }

//    private fun submitSale(selectedItems: Map<Int, Int>) {
//        val subTotal = calculateSubtotal(selectedItems)
//        val discountAmount = subTotal * (discountPercent / 100.0)
//        val totalAmount = (subTotal - discountAmount).coerceAtLeast(0.0)
//
//        val items = selectedItems.map { (productId, qty) ->
//            SaleItem(product_id = productId, qty = qty)
//        }
//
//        val request = SaleRequest(
//            shop_id = shopId,
//            area_id = areaId,
//            sub_total = subTotal,
//            discount = discountAmount,
//            total = totalAmount,
//            cash_received = totalAmount,
//            items = items
//        )
//
//        saleViewModel.createNewSale(request).observe(this) { response ->
//            when (response.status) {
//                Status.LOADING -> AppUtil.startLoader(this)
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val res = response.data
//                    if (res != null && res.isSuccessful) {
//                        val body = res.body()
//                        if (body?.result == "success") {
//                            Toast.makeText(this, "Sale created successfully", Toast.LENGTH_SHORT).show()
//                            finish()
//                        } else {
//                            Toast.makeText(this, body?.message ?: "Failed to create sale", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    Toast.makeText(this, response.message ?: "Network Error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupRecycler() {
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = RiderProductAdapter(productList, quantityMap) { subtotal, totalEggs ->
            updateTotal(subtotal, totalEggs)
        }
    }
    private fun loadProducts() {
        viewModel.getRiderProducts().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<RiderProductData>?
                        if (baseResponse?.result == "success" && baseResponse.data != null) {
                            productList = baseResponse.data.picked_items
                            binding.recyclerProducts.adapter =
                                RiderProductAdapter(productList, quantityMap) { subtotal, totalEggs ->
                                    updateTotal(subtotal, totalEggs)
                                }

                        } else {
                            Toast.makeText(this, baseResponse?.message ?: "Failed to fetch products", Toast.LENGTH_SHORT).show()
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

    private fun updateTotal(subtotal: Double, totalEggs: Int) {
        val eggsPerPatti = 360
        val numberOfPattis = totalEggs / eggsPerPatti   // integer division
        val discountAmount = numberOfPattis * discountPercent   // discount per patti
        val totalAfterDiscount = (subtotal - discountAmount).coerceAtLeast(0.0)

        // Update UI
        binding.tvSubtotal.text = "Rs. %.2f".format(subtotal)
        binding.tvDiscount.text = "Rs. %.2f".format(discountAmount)
        binding.tvTotal.text = "Rs. %.2f".format(totalAfterDiscount)

        // Store for intent
        currentSubtotal = subtotal
        currentTotalEggs = totalEggs
        currentNumberOfPattis = numberOfPattis
        currentDiscountAmount = discountAmount
        currentTotalAfterDiscount = totalAfterDiscount
    }


    private fun calculateSubtotal(selectedItems: Map<Int, Int>): Double {
        var subtotal = 0.0
        selectedItems.forEach { (productId, qty) ->
            val product = productList.find { it.product_id == productId }
            subtotal += (product?.price?.toDoubleOrNull() ?: 0.0) * qty
        }
        return subtotal
    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
    }


    private fun selectButton(selected: LinearLayout) {


        if (selected == binding.cashLayout) {

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
