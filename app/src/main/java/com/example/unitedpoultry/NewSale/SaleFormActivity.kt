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
import com.example.unitedpoultry.NewSale.Adapter.NewSaleAdapter
import com.example.unitedpoultry.NewSale.Adapter.RiderProductAdapter
import com.example.unitedpoultry.NewSale.model.Damage
import com.example.unitedpoultry.NewSale.model.DamageEggsRequest
import com.example.unitedpoultry.NewSale.model.LiquidItem
import com.example.unitedpoultry.NewSale.model.QtyRequest
import com.example.unitedpoultry.NewSale.model.RiderProductUI
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.NewSale.viewmodel.RiderNewSaleViewModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.PickedToday
import com.example.unitedpoultry.rider_home.model.PickedTodayProduct
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


    private val quantityMap = mutableMapOf<Int, Int>()
    private var productList = listOf<RiderProductUI>()
    private var discountPerPetti: Double = 0.0

    private var shopId: Int = 0
    private var areaId: Int = 0

    private var name: String = ""
    private var address: String = ""

    private var paymentType = "cash"



    private var currentSubtotal: Double = 0.0
    private var currentTotalEggs: Int = 0
    private var currentNumberOfPattis: Int = 0
    private var currentDiscountAmount: Double = 0.0
    private var currentTotalAfterDiscount: Double = 0.0




    private val productViewModel: GetProductViewModel by viewModel()



    private val expireQuantityMap = mutableMapOf<Int, Int>()
    private val returnQuantityMap = mutableMapOf<Int, Int>()

    private var allproductList = listOf<com.example.unitedpoultry.adminproduct.model.Product>()

    private lateinit var expireAdapter: NewSaleAdapter
    private lateinit var returnAdapter: NewSaleAdapter

    private var liquidKg: Double = 0.0


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

        discountPerPetti = intent.getStringExtra("DISCOUNT")?.toDoubleOrNull() ?: 0.0

        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)

        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"


        setupRecycler()
        setupExpireRecycler()
        setupReturnRecycler()

        setupClicks()
        showData()
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


            if (validateInputs()) {

                if(paymentType=="cash") {
                   submitSale()
                }else{
                    submitSaleCheque()
                }
            }
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
        loadAllProducts()
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

                        val baseResponse = res.body() as BaseResponse<PickedToday>?

                        if (baseResponse?.result == "success" && baseResponse.data != null) {

                            val data = baseResponse.data

                            val remainingMap = data.remaining

                            productList = data.products.mapNotNull { product ->

                                val remainingQty = remainingMap.entries.find {
                                    it.key.equals(product.name, ignoreCase = true)
                                }?.value ?: 0



                                if (remainingQty > 0) {
                                    val basePrice = product.latest_price ?: 0.0

                                    val finalPrice = when {
                                        product.name.equals("Petti", ignoreCase = true) -> {
                                            basePrice - discountPerPetti
                                        }

                                        product.name.equals("Tray", ignoreCase = true) -> {
                                            val pettiBasePrice = data.products.find {
                                                it.name.equals("Petti", ignoreCase = true)
                                            }?.latest_price ?: 0.0

                                            val adjustedPettiPrice = pettiBasePrice - discountPerPetti
                                            adjustedPettiPrice / 12.0
                                        }

                                        // For carton, shapper, liquid, etc., leave the price as-is
                                        else -> basePrice
                                    }

                                    // 3. Map it to your UI Model with the updated price
                                    RiderProductUI(
                                        id = product.id,
                                        name = product.name,
                                        price = finalPrice, // Passing the newly calculated price here
                                        remainingQty = remainingQty
                                    )
                                } else null
                            }


                            if (productList.isEmpty()) {

                                binding.productLayout.visibility = View.GONE
                                binding.tvEmptyProducts.visibility = View.VISIBLE

                            } else {

                                binding.productLayout.visibility = View.VISIBLE
                                binding.tvEmptyProducts.visibility = View.GONE

                                binding.recyclerProducts.layoutManager = LinearLayoutManager(this)

                                binding.recyclerProducts.adapter =
                                    RiderProductAdapter(productList, quantityMap) { subtotal, totalEggs ->
                                        updateTotal(subtotal, totalEggs)
                                    }
                            }

                        } else {
                            Toast.makeText(
                                this,
                                baseResponse?.message ?: "Failed to fetch products",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(
                        this,
                        response.message ?: "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }




    private fun setupExpireRecycler() {
        binding.recyclerExpireProducts.layoutManager = LinearLayoutManager(this)

        expireAdapter = NewSaleAdapter(allproductList, expireQuantityMap)
        binding.recyclerExpireProducts.adapter = expireAdapter
    }

    private fun setupReturnRecycler() {
        binding.recyclerReturnProducts.layoutManager = LinearLayoutManager(this)

        returnAdapter = NewSaleAdapter(allproductList, returnQuantityMap)
        binding.recyclerReturnProducts.adapter = returnAdapter
    }

    private fun loadAllProducts() {
        productViewModel.getProducts().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val res = response.data
                    if (res != null && res.isSuccessful) {
                        val baseResponse = res.body() as BaseResponse<ProductData>?
                        if (baseResponse?.result == "success" && baseResponse.data != null) {

                            allproductList = baseResponse.data.products

                            expireAdapter = NewSaleAdapter(allproductList, expireQuantityMap)
                            returnAdapter = NewSaleAdapter(allproductList, returnQuantityMap)

                            binding.recyclerExpireProducts.adapter = expireAdapter
                            binding.recyclerReturnProducts.adapter = returnAdapter

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
        // 1. Use .contains() instead of .equals() since the name string now contains prices
        val pettiProduct = productList.find { it.name.contains("Petti", ignoreCase = true) }
        val trayProduct = productList.find { it.name.contains("Tray", ignoreCase = true) }

        val pettiId = pettiProduct?.id
        val trayId = trayProduct?.id

        val pettiQty = if (pettiId != null) quantityMap[pettiId] ?: 0 else 0
        val trayQty = if (trayId != null) quantityMap[trayId] ?: 0 else 0

        val totalPettiDiscount = pettiQty * discountPerPetti
        val totalTrayDiscount = trayQty * (discountPerPetti / 12.0)
        val totalCalculatedDiscount = totalPettiDiscount + totalTrayDiscount

        val originalSubtotal = subtotal + totalCalculatedDiscount
        val totalAfterDiscount = subtotal


        val discountText = when {
            pettiQty > 0 && trayQty > 0 -> {
                "Petti: Rs. %.0f | Tray: Rs. %.0f".format(totalPettiDiscount, totalTrayDiscount)
            }
            pettiQty > 0 -> {
                "Petti Disc: Rs. %.0f".format(totalPettiDiscount)
            }
            trayQty > 0 -> {
                "Tray Disc: Rs. %.0f".format(totalTrayDiscount)
            }
            else -> {
                "Rs. 0.00"
            }
        }


        binding.tvSubtotal.text = "Rs. %.2f".format(originalSubtotal)

        binding.tvDiscount.text = discountText

        binding.tvTotal.text = "Rs. %.2f".format(totalAfterDiscount)

        binding.etBorrowedAmount.setText(formatAmount(totalAfterDiscount))
        binding.etCollectionAmount.setText("")

        currentSubtotal = originalSubtotal
        currentDiscountAmount = totalCalculatedDiscount
        currentTotalAfterDiscount = totalAfterDiscount
    }

    //    private fun calculateSubtotal(selectedItems: Map<Int, Int>): Double {
//        var subtotal = 0.0
//        selectedItems.forEach { (productId, qty) ->
//            val product = productList.find { it.product_id == productId }
//            subtotal += (product?.price?.toDoubleOrNull() ?: 0.0) * qty
//        }
//        return subtotal
//    }
    fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            "%.0f".format(amount)   // whole number → no decimal
        } else {
            "%.2f".format(amount)   // decimal → 2 places
        }
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

                // Treat empty EditText as 0
                var collection = s.toString().toDoubleOrNull() ?: 0.0

                val remaining = (total - collection).coerceAtLeast(0.0)

                val remainingText = formatAmount(remaining)

                binding.etBorrowedAmount.setText(remainingText)
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
        //  binding.etNoteError.visibility = View.GONE

        //binding.etTrayQuantityError.visibility = View.GONE


        if (selectedImageFile == null && paymentType == "online_cheque") {
            binding.imageError.visibility = View.VISIBLE
            binding.imageError.text = "Add Image"
            valid = false
        }


//        val note = binding.etNote.text.toString().trim()
//        if (note.isEmpty() && paymentType == "online_cheque") {
//            binding.etNoteError.visibility = View.VISIBLE
//            binding.etNoteError.text = "Enter note"
//            valid = false
//        }

//        val tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
//        val peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0
//        val totalTrayQty = tray + (peti * traysPerPeti)

//        // 1️⃣ No stock remaining
//        if (remainingQty <= 0) {
//            Toast.makeText(this, "No stock remaining", Toast.LENGTH_SHORT).show()
//            valid = false
//        }
//
//
//        if (totalTrayQty > remainingQty) {
//            Toast.makeText(this, "Not enough stock available", Toast.LENGTH_SHORT).show()
//            valid = false
//        }
//        if (tray > 11) {
//            binding.etTrayQuantityError.visibility = View.VISIBLE
//            binding.etTrayQuantityError.text = "Cannot exceed 11 trays"
//            valid = false
//        }
//
//        if (tray == 0 && peti == 0) {
//            binding.etTrayQuantityError.visibility = View.VISIBLE
//            binding.etTrayQuantityError.text = "Please enter tray or peti quantity"
//            valid = false
//        }


//        if (totalTrayQty > remainingQty) {
//            binding.etTrayQuantityError.visibility = View.VISIBLE
//            binding.etTrayQuantityError.text = "Not enough trays available"
//            valid = false
//        }

        return valid
    }


    private fun buildItemsList(): List<Map<String, Int>> {
        return quantityMap
            .filter { it.value > 0 }
            .map { (productId, qty) ->
                mapOf(
                    "product_id" to productId,
                    "qty" to qty
                )
            }
    }


    private fun buildRequest(): Damage {

        liquidKg = binding.etLiquidKg.text.toString().toDoubleOrNull() ?: 0.0

        val expire = expireQuantityMap
            .filter { it.value > 0 }
            .map {
                QtyRequest(
                    product_id = it.key,
                    qty = it.value
                )
            }

        val returnList = returnQuantityMap
            .filter { it.value > 0 }
            .map {
                QtyRequest(
                    product_id = it.key,
                    qty = it.value
                )
            }

        return Damage(
            expire = expire,
            returnData = returnList,
            liquid = LiquidItem(liquidKg)
        )
    }

//    private fun buildExpireMap(): Map<String, Int> {
//        val result = mutableMapOf<String, Int>()
//
//        quantityMap.forEach { (productId, qty) ->
//            val productName = allproductList
//                .find { it.id == productId }
//                ?.name
//                ?.lowercase()
//                ?: return@forEach
//
//            result[productName] = qty
//        }
//
//        return result
//    }
//
//
//    private fun buildExpireMap(): Map<String, Int> {
//        val result = mutableMapOf<String, Int>()
//
//        quantityMap.forEach { (productId, qty) ->
//            val productName = allproductList
//                .find { it.id == productId }
//                ?.name
//                ?.lowercase()
//                ?: return@forEach
//
//            result[productName] = qty
//        }
//
//        return result
//    }
//
//
//    private fun buildDamageEggs(): Map<String, Any> {
//        return mapOf(
//            "expire" to buildExpireMap()
//        )
//    }

    private fun submitSale() {

        val gson = Gson()

        val itemsList = buildItemsList()

        if (itemsList.isEmpty()) {
            Toast.makeText(this, "Please select at least one product", Toast.LENGTH_SHORT).show()
            return
        }


        val itemsJson = gson.toJson(itemsList)
        val itemsBody = itemsJson.toRequestBody("application/json".toMediaTypeOrNull())

        val damageEggs = buildRequest()

        val damageEggsJson = gson.toJson(damageEggs)
        val damageEggsBody = damageEggsJson.toRequestBody("application/json".toMediaTypeOrNull())





        // ---------------- BASIC FIELDS ----------------
        val shop_id = shopId.toString().toPart()
        val area_id = areaId.toString().toPart()
        val payment_type = paymentType.toPart()
        val collectionAmountValue = binding.etCollectionAmount.text.toString().trim().toDoubleOrNull() ?: 0
        val collection_amount = collectionAmountValue.toString().toPart()
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

                        // Convert API response 'data' to JSON
                        val jsonData = Gson().toJson(response.body()?.data)

                        // Pass JSON to next activity
                        val intent = Intent(this, SaleSuccessActivity::class.java)
                        intent.putExtra("sale_data_json", jsonData)
                        startActivity(intent)
                        finish()

                    } else {
                        val msg = response?.body()?.message ?: "Failed to create sale"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {

                    Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
                }

                Status.LOADING -> { /* Already handled */ }
            }
        }
    }






    private fun submitSaleCheque() {

        val gson = Gson()

        val itemsList = buildItemsList()

        if (itemsList.isEmpty()) {
            Toast.makeText(this, "Please select at least one product", Toast.LENGTH_SHORT).show()
            return
        }


        val itemsJson = gson.toJson(itemsList)
        val itemsBody = itemsJson.toRequestBody("application/json".toMediaTypeOrNull())

        val damageEggs = buildRequest()

        val damageEggsJson = gson.toJson(damageEggs)
        val damageEggsBody = damageEggsJson.toRequestBody("application/json".toMediaTypeOrNull())




        val shop_id = shopId.toString().toPart()
        val area_id = areaId.toString().toPart()
        val payment_type = paymentType.toPart()



        val collectionAmountValue = binding.etCollectionAmount.text.toString().trim().toDoubleOrNull() ?: 0
        val collection_amount = collectionAmountValue.toString().toPart()

        val borrowed_amount = binding.etBorrowedAmount.text.toString().trim().toPart()

//        val image: MultipartBody.Part? = selectedImageFile?.let {
//            MultipartBody.Part.createFormData(
//                "image",
//                it.name,
//                it.asRequestBody("image/*".toMediaTypeOrNull())
//            )
//        }

//        val payment_record: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "payment_record",
//            selectedImageFile!!.name,
//            selectedImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
//        )

        val imageFile = selectedImageFile ?: run {
            Toast.makeText(this, "Image required", Toast.LENGTH_SHORT).show()
            return
        }

        val payment_record = MultipartBody.Part.createFormData(
            "payment_record",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        )


        val note = binding.etNote.text.toString().trim().toPart()


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

                        // Convert API response 'data' to JSON
                        val jsonData = Gson().toJson(response.body()?.data)

                        // Pass JSON to next activity
                        val intent = Intent(this, SaleSuccessActivity::class.java)
                        intent.putExtra("sale_data_json", jsonData)
                        startActivity(intent)
                        finish()

                    } else {
                        val msg = response?.body()?.message ?: "Failed to create sale"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    Toast.makeText(this, "Network connection problem. Please try again.", Toast.LENGTH_SHORT).show()
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

