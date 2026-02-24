package com.example.unitedpoultry.Collection

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.NewSale.SaleConfirmationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityCollectionformBinding
import java.io.File
import java.io.FileOutputStream

class CollectionformActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionformBinding
    private lateinit var adapter: SelectShopAdapter

    private var discountPercent: Double = 0.0

    private var shopId: Int = 0
    private var areaId: Int = 0

    private var name: String = ""
    private var address: String = ""

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
        binding.backArrow.setOnClickListener {
            finish()
        }

//        intent.putExtra("SHOP_ID", item.id)
//        intent.putExtra("AREA_ID", areaId)
//        intent.putExtra("NAME", item.name)
//        intent.putExtra("ADDRESS", item.address)
//        intent.putExtra("DISCOUNT", item.discount_per_petti)


        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)

        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"


        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)
       // binding.tvBalance.text = "Rs. $balance"


        setupClicks()

        resetImageViews()
        selectButton(binding.cashLayout)
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

        binding.btnConfirmCollection.setOnClickListener {

            val intent = Intent(this, CollectionSuccessActivity::class.java)

            intent.putExtra("SHOP_NAME", name)
            intent.putExtra("ADDRESS", address)
            intent.putExtra("INITIALS", getInitials(name))
            startActivity(intent)
        }


//        binding.btnConfirmSale.setOnClickListener {
//
//            val selectedItems = quantityMap.filter { it.value > 0 }
//            if (selectedItems.isEmpty()) {
//                Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//
//            val intent = Intent(this, SaleConfirmationActivity::class.java)
//
//            val totalQuantity = selectedItems.values.sum()
//
//
//            intent.putExtra("SHOP_NAME", name)
//            intent.putExtra("ADDRESS", address)
//            intent.putExtra("INITIALS", getInitials(name))
//
//            intent.putExtra("SHOP_ID", shopId)
//            intent.putExtra("AREA_ID", areaId)
//            intent.putExtra("SUB_TOTAL", currentSubtotal)
//            intent.putExtra("DISCOUNT", currentDiscountAmount)
//            intent.putExtra("TOTAL", currentTotalAfterDiscount)
//            intent.putExtra("TOTAL_QUANTITY", totalQuantity)
//
//
//            // Pass product IDs & quantities
//            intent.putIntegerArrayListExtra(
//                "PRODUCT_IDS",
//                ArrayList(selectedItems.keys)
//            )
//
//            intent.putIntegerArrayListExtra(
//                "QUANTITIES",
//                ArrayList(selectedItems.values)
//            )
//
//            startActivity(intent)
//        }

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





    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
