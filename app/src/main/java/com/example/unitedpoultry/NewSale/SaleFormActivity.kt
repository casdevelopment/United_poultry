package com.example.unitedpoultry.NewSale

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.RiderProductAdapter
import com.example.unitedpoultry.NewSale.Adapter.ShowPickedProductSaleAdapter
import com.example.unitedpoultry.NewSale.model.PickedItemsResponse
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductData
import com.example.unitedpoultry.NewSale.model.SaleProduct
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.NewSale.viewmodel.RiderNewSaleViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
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

class SaleFormActivity : BaseActivity() {

    private lateinit var binding: ActivitySaleFormBinding
    private val viewModel: GetRiderProductViewModel by viewModel()
    private val saleViewModel: RiderNewSaleViewModel by viewModel()

    private var productList = listOf<Product>()
    private var discountPercent: Double = 0.0

    private var shopId: Int = 0
    private var areaId: Int = 0

    private var name: String = ""
    private var address: String = ""

    private var paymentType = "cash"
    private var discountPerPeti: Double = 0.0


    private var currentSubtotal: Double = 0.0
    private var currentTotalEggs: Int = 0
    private var currentNumberOfPattis: Int = 0
    private var currentDiscountAmount: Double = 0.0
    private var currentTotalAfterDiscount: Double = 0.0


    private val quantityMap = mutableMapOf<Int, Int>()
    private val saleProducts = mutableListOf<SaleProduct>()
    private lateinit var adapter: ShowPickedProductSaleAdapter

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

        discountPerPeti = intent.getStringExtra("DISCOUNT")?.toDoubleOrNull() ?: 0.0


        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)


        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"


        setupRecycler()
        setupClicks()
        showData()
        setupDamageTextWatchers()
        setupCollectionWatcher()
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

            if (validateInputs()) {

                if(paymentType=="cash") {
                    submitSale()
                }else{
                    submitSaleCheque()
                }
            }



//            val subTotal = calculateSubtotal(selectedItems)
//            val discountAmount = subTotal * (discountPercent / 100.0)
//            val totalAmount = (subTotal - discountAmount).coerceAtLeast(0.0)

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
        }

    }



    override fun onResume() {
        super.onResume()
        loadProducts()
    }


    fun mapToSaleProducts(response: PickedItemsResponse): List<SaleProduct> {
        val list = mutableListOf<SaleProduct>()
        val remaining = response.remaining

        response.products.forEach { product ->
            val name = product.name.lowercase().trim()

            val remainingQty = when (name) {
                "peti" -> remaining.total_peti
                "tray" -> remaining.total_trays
                else -> 0
            }

            if (remainingQty > 0) {
                list.add(
                    SaleProduct(
                        id = product.id,
                        name = product.name,
                        remainingQty = remainingQty,
                        price = product.latest_price
                    )
                )
            }
        }

        return list
    }


    private fun setupRecycler() {
        adapter = ShowPickedProductSaleAdapter(
            saleProducts,
            discountPerPeti,
            quantityMap
        ) { subtotal, discount, total, _productQuantities ->
            binding.tvSubtotal.text = "Rs. %.2f".format(subtotal)
            binding.tvDiscount.text = "Rs. %.2f".format(discount)
            binding.tvTotal.text = "Rs. %.2f".format(total)

            val enteredCollection = binding.etCollectionAmount.text.toString().toDoubleOrNull() ?: 0.0
            val collection = enteredCollection.coerceAtMost(total)
            binding.etCollectionAmount.setText("%.2f".format(collection))

            val remaining = (total - collection).coerceAtLeast(0.0)
            binding.etBorrowedAmount.setText("%.2f".format(remaining))
        }

        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter
    }

    private fun loadProducts() {
        viewModel.getRiderProducts().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val base = res.body() as BaseResponse<PickedItemsResponse>?
                        if (base?.result == "success" && base.data != null) {
                            val mappedList = mapToSaleProducts(base.data)
                            saleProducts.clear()
                            saleProducts.addAll(mappedList)
                            adapter.notifyDataSetChanged()

                            binding.tvEmptyProducts.visibility =
                                if (saleProducts.isEmpty()) View.VISIBLE else View.GONE
                            binding.recyclerProducts.visibility =
                                if (saleProducts.isEmpty()) View.GONE else View.VISIBLE

                        } else {
                            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show()
                            binding.tvEmptyProducts.visibility = View.VISIBLE
                            binding.recyclerProducts.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupDamageTextWatchers() {
        val damageFields: Map<String, Map<String, android.widget.EditText>> = mapOf(
            "expire" to mapOf(
                "peti" to binding.etExpirePeti,
                "tray" to binding.etExpireTray,
                "single" to binding.etExpireSingle
            ),
            "return" to mapOf(
                "peti" to binding.etReturnPeti,
                "tray" to binding.etReturnTray,
                "single" to binding.etReturnSingle
            ),
            "liquid" to mapOf(
                "peti" to binding.etLiquidPeti,
                "tray" to binding.etLiquidTray,
                "single" to binding.etLiquidSingle
            )
        )

        damageFields.forEach { (_, map) ->
            map.forEach { (_, editText) ->
                // Initialize with "0"
                if (editText.text.isEmpty()) editText.setText("0")

                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        // Update TextViews whenever a value changes
                        updateDamageTextViews()
                    }
                })
            }
        }

        // Initialize display
        updateDamageTextViews()
    }

    private fun updateDamageTextViews() {
        val damageMap = getDamageEggsMap()

        binding.tvExpire.text = "Peti:${damageMap["expire"]?.get("peti") ?: 0} " +
                "Tray:${damageMap["expire"]?.get("tray") ?: 0} " +
                "Single:${damageMap["expire"]?.get("single") ?: 0}"

        binding.tvReturn.text = "Peti:${damageMap["return"]?.get("peti") ?: 0} " +
                "Tray:${damageMap["return"]?.get("tray") ?: 0} " +
                "Single:${damageMap["return"]?.get("single") ?: 0}"

        binding.tvLiquid.text = "Peti:${damageMap["liquid"]?.get("peti") ?: 0} " +
                "Tray:${damageMap["liquid"]?.get("tray") ?: 0} " +
                "Single:${damageMap["liquid"]?.get("single") ?: 0}"
    }

    private fun getDamageEggsMap(): Map<String, Map<String, Int>> {
        return mapOf(
            "expire" to mapOf(
                "peti" to (binding.etExpirePeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etExpireTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etExpireSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "return" to mapOf(
                "peti" to (binding.etReturnPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etReturnTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etReturnSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "liquid" to mapOf(
                "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
            )
        )
    }

    private fun setupCollectionWatcher() {
        binding.etCollectionAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val total = binding.tvTotal.text.toString()
                    .replace("Rs.", "")
                    .trim()
                    .toDoubleOrNull() ?: 0.0

                var collection = s.toString().toDoubleOrNull() ?: 0.0
                if (collection > total) {
                    collection = total
                    binding.etCollectionAmount.setText("%.2f".format(collection))
                    binding.etCollectionAmount.setSelection(binding.etCollectionAmount.text.length)
                }

                val remaining = (total - collection).coerceAtLeast(0.0)
                binding.etBorrowedAmount.setText("%.2f".format(remaining))
            }
        })
    }



    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
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


    private fun validateInputs(): Boolean {
        var valid = true

        binding.imageError.visibility = View.GONE
        binding.etNoteError.visibility = View.GONE


        if (selectedImageFile == null && paymentType == "online_cheque") {
            binding.imageError.visibility = View.VISIBLE
            binding.imageError.text = "Add Image"
            valid = false
        }


        val note = binding.etNote.text.toString().trim()
        if (note.isEmpty() && paymentType == "online_cheque") {
            binding.etNoteError.visibility = View.VISIBLE
            binding.etNoteError.text = "Enter note"
            valid = false
        }
//
//        val Collected = binding.etCollectionAmount.text.toString().trim()
//        if (Collected.isEmpty()) {
//            binding.etCollectionAmountError.visibility = View.VISIBLE
//            binding.etCollectionAmountError.text = "Enter Collected Amount"
//            valid = false
//        }


        return valid
    }

    private fun submitSale() {
        val selectedItems = quantityMap.filter { it.value > 0 }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        val gson = Gson()

        // ---------------- ITEMS JSON ----------------
        val itemsList = selectedItems.map { (productId, qty) ->
            mapOf("product_id" to productId, "qty" to qty)
        }
        val itemsJson = gson.toJson(itemsList)
        val itemsBody = itemsJson.toRequestBody("application/json".toMediaTypeOrNull())

        // ---------------- DAMAGE EGGS JSON ----------------
        val damageEggsMap: Map<String, Map<String, Int>> = mapOf(
            "expire" to mapOf(
                "peti" to (binding.etExpirePeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etExpireTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etExpireSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "return" to mapOf(
                "peti" to (binding.etReturnPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etReturnTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etReturnSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "liquid" to mapOf(
                "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
            )
        )

        val damageEggsJson = gson.toJson(damageEggsMap)
        val damageEggsBody = damageEggsJson.toRequestBody("application/json".toMediaTypeOrNull())

        // ---------------- BASIC FIELDS ----------------
        val shop_id = shopId.toString().toPart()
        val area_id = areaId.toString().toPart()
        val payment_type = paymentType.toPart()
        val collection_amount = binding.etCollectionAmount.text.toString().trim().toPart()
        val borrowed_amount = binding.etBorrowedAmount.text.toString().trim().toPart()

        // ---------------- API CALL ----------------
        AppUtil.startLoader(this)

        saleViewModel.createNewSale(
            shop_id,
            area_id,
            payment_type,
            collection_amount,
            borrowed_amount,
            itemsBody,
            damageEggsBody
        ).observe(this) { apiResponse ->
            AppUtil.stopLoader()

            when (apiResponse.status) {
                Status.SUCCESS -> {
                    val response = apiResponse.data
                    if (response?.isSuccessful == true && response.body()?.result == "success") {
                        Toast.makeText(this, response.body()?.message ?: "Sale created", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response?.body()?.message ?: "Failed to create sale"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }

                Status.LOADING -> { /* Already handled */ }
            }
        }
    }

    private fun submitSaleCheque() {

        val selectedItems = quantityMap.filter { it.value > 0 }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please pick at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        val gson = Gson()

        val itemsList = selectedItems.map { (productId, qty) ->
            mapOf("product_id" to productId, "qty" to qty)
        }
        val itemsJson = gson.toJson(itemsList)
        val itemsBody = itemsJson.toRequestBody("application/json".toMediaTypeOrNull())

        // ---------------- DAMAGE EGGS JSON ----------------
        val damageEggsMap: Map<String, Map<String, Int>> = mapOf(
            "expire" to mapOf(
                "peti" to (binding.etExpirePeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etExpireTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etExpireSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "return" to mapOf(
                "peti" to (binding.etReturnPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etReturnTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etReturnSingle.text.toString().toIntOrNull() ?: 0)
            ),
            "liquid" to mapOf(
                "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
                "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
            )
        )

        val damageEggsJson = gson.toJson(damageEggsMap)
        val damageEggsBody = damageEggsJson.toRequestBody("application/json".toMediaTypeOrNull())

        // ---------------- BASIC FIELDS ----------------
        val shop_id = shopId.toString().toPart()
        val area_id = areaId.toString().toPart()
        val payment_type = paymentType.toPart()
        val collection_amount = binding.etCollectionAmount.text.toString().trim().toPart()
        val borrowed_amount = binding.etBorrowedAmount.text.toString().trim().toPart()

//        val image: MultipartBody.Part? = selectedImageFile?.let {
//            MultipartBody.Part.createFormData(
//                "image",
//                it.name,
//                it.asRequestBody("image/*".toMediaTypeOrNull())
//            )
//        }

        val payment_record: MultipartBody.Part = MultipartBody.Part.createFormData(
            "payment_record",
            selectedImageFile!!.name,
            selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        )


        val note = binding.etCollectionAmount.text.toString().trim().toPart()

        // ---------------- API CALL ----------------
        AppUtil.startLoader(this)

        saleViewModel.createNewSaleCheque(
            shop_id,
            area_id,
            itemsBody,
            damageEggsBody,
            payment_type,
            collection_amount,
            borrowed_amount,
            payment_record,
            note
        ).observe(this) { apiResponse ->
            AppUtil.stopLoader()

            when (apiResponse.status) {
                Status.SUCCESS -> {
                    val response = apiResponse.data
                    if (response?.isSuccessful == true && response.body()?.result == "success") {
                        Toast.makeText(this, response.body()?.message ?: "Sale created", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response?.body()?.message ?: "Failed to create sale"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }

                Status.LOADING -> { /* Already handled */ }
            }
        }
    }


    private fun String.toPart(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
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
