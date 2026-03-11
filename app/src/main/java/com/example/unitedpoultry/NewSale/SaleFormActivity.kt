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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.ShowPickedProductSaleAdapter
import com.example.unitedpoultry.NewSale.model.PickedItemsResponse
import com.example.unitedpoultry.NewSale.model.Product
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

    private var remainingQty = 0
    private val traysPerPeti = 12
    private var stockPeti = 0

    private var trayProductObj: SaleProduct? = null

    private var isUpdatingTray = false
    private var isUpdatingPeti = false

    private var productList = listOf<Product>()
    private var discountPercent: Double = 0.0

    private var shopId: Int = 0
    private var areaId: Int = 0

    private var name: String = ""
    private var address: String = ""

    private var paymentType = "cash"
    private var discountPerPeti: Double = 0.0

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

        discountPerPeti = intent.getStringExtra("DISCOUNT")?.toDoubleOrNull() ?: 0.0

        configureStatusBar(isLightBackground = true, colorResId = android.R.color.white)

        shopId = intent.getIntExtra("SHOP_ID", 0)
        areaId = intent.getIntExtra("AREA_ID", 0)


        name = intent.getStringExtra("NAME") ?: "N/A"
        address = intent.getStringExtra("ADDRESS") ?: "N/A"


        loadProducts()
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
        binding.tvSubtotal.text = "Rs 0"
        binding.tvTotal.text = "Rs 0"
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


//    override fun onResume() {
//        super.onResume()
//        loadProducts()
//    }



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

                            remainingQty = base.data.remaining.total_trays


                            if (remainingQty <= 0) {
                                binding.productLayout.visibility = View.GONE
                                binding.tvEmptyProducts.visibility = View.VISIBLE
                                return@observe
                            }

                            binding.productLayout.visibility = View.VISIBLE
                            binding.tvEmptyProducts.visibility = View.GONE

                            val trayProduct = base.data.products.find { it.name.equals("Tray", true) }

                            trayProduct?.let { tray ->

                                trayProductObj = SaleProduct(
                                    id = tray.id,
                                    name = tray.name,
                                    price = tray.latest_price,
                                    remainingQty = remainingQty
                                )

                                stockPeti = remainingQty / traysPerPeti

                                binding.trayLayout.visibility = View.VISIBLE
                                binding.petiLayout.visibility =
                                    if (stockPeti > 0) View.VISIBLE else View.GONE

//                                binding.etTrayQuantity.text.clear()
//                                binding.etPetiQuantity.text.clear()

                                setupQuantityWatchers()
                            }

                        } else {
                            binding.productLayout.visibility = View.GONE
                            binding.tvEmptyProducts.visibility = View.VISIBLE
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

    private fun setupQuantityWatchers() {

        binding.etTrayQuantity.addTextChangedListener(object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable?) {

                if (isUpdatingTray) return
                isUpdatingTray = true

                var tray = s.toString().toIntOrNull() ?: 0
                var peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0

//                if (tray > 11){
//
//                    Toast.makeText(this@SaleFormActivity, "Tray cannot be more than 11", Toast.LENGTH_SHORT).show()
//
//                    tray = 11
//                }

                if (tray > 11) {
                    binding.etTrayQuantityError.visibility = View.VISIBLE
                    binding.etTrayQuantityError.text = "Cannot exceed 11 trays"

                    tray = 11
                } else {
                    binding.etTrayQuantityError.visibility = View.GONE
                }

                val total = tray + peti * traysPerPeti

                if (total > remainingQty) {

                    tray = (remainingQty - peti * traysPerPeti).coerceAtLeast(0)

                    Toast.makeText(
                        this@SaleFormActivity,
                        "Tray adjusted because of Peti",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                setNumberOrHint(binding.etTrayQuantity, tray)

                val remainingForPeti = remainingQty - tray

                stockPeti = remainingForPeti / traysPerPeti

                binding.petiLayout.visibility =
                    if (stockPeti > 0) View.VISIBLE else View.GONE

                if (peti > stockPeti) {

                    peti = stockPeti

                    setNumberOrHint(binding.etPetiQuantity, peti)
                }

                isUpdatingTray = false

                calculateTotals()
            }
        })


        binding.etPetiQuantity.addTextChangedListener(object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable?) {

                if (isUpdatingPeti) return
                isUpdatingPeti = true

                var peti = s.toString().toIntOrNull() ?: 0
                var tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0

                val remainingForPeti = remainingQty - tray

                val maxPeti = remainingForPeti / traysPerPeti

                if (peti > maxPeti) {

                    peti = maxPeti

                    binding.etPetiQuantityError.visibility = View.VISIBLE
                    binding.etPetiQuantityError.text = "Cannot order more Peti than available"

                }else {
                    binding.etPetiQuantityError.visibility = View.GONE
                }

                setNumberOrHint(binding.etPetiQuantity, peti)

                val total = tray + peti * traysPerPeti

                if (total > remainingQty) {

                    tray = (remainingQty - peti * traysPerPeti).coerceAtLeast(0)

                    setNumberOrHint(binding.etTrayQuantity, tray)
                }

                isUpdatingPeti = false

                calculateTotals()
            }
        })
    }

    private fun calculateTotals() {

        val tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
        val peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0

        val totalTrays = tray + (peti * traysPerPeti)

        val price = trayProductObj?.price ?: 0.0

        val subtotal = totalTrays * price

        val discountAmount = peti * discountPerPeti

        val total = subtotal - discountAmount

        binding.tvSubtotal.text = "Rs. ${formatAmount(subtotal)}"

        binding.tvDiscount.text = "Rs. ${formatAmount(discountAmount)}"

        binding.tvTotal.text = "Rs. ${formatAmount(total)}"

        binding.etBorrowedAmount.setText(formatAmount(total))

        binding.etCollectionAmount.text.clear()
    }

    fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            "%.0f".format(amount)   // whole number → no decimal
        } else {
            "%.2f".format(amount)   // decimal → 2 places
        }
    }

    private fun setNumberOrHint(editText: EditText, value: Int) {

        val text = if (value == 0) "" else value.toString()

        if (editText.text.toString() != text) {
            editText.setText(text)
            editText.setSelection(editText.text.length)
        }
    }

    abstract class SimpleTextWatcher : android.text.TextWatcher {

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
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
//                "peti" to binding.etLiquidPeti,
//                "tray" to binding.etLiquidTray,
                "kg" to binding.etLiquidKg
            )
        )

        damageFields.forEach { (_, map) ->
            map.forEach { (_, editText) ->
                // Initialize with "0"
                if (editText.text.isEmpty()) editText.hint = "Amount"

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

//        binding.tvLiquid.text = "Peti:${damageMap["liquid"]?.get("peti") ?: 0} " +
//                "Tray:${damageMap["liquid"]?.get("tray") ?: 0} " +
//                "Single:${damageMap["liquid"]?.get("single") ?: 0}"

        binding.tvLiquid.text = "Kg:${damageMap["liquid"]?.get("kg") ?: 0}"
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
//            "liquid" to mapOf(
////                "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
////                "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
//                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
//            )

            "liquid" to mapOf(
                "kg" to (binding.etLiquidKg.text.toString().toIntOrNull() ?: 0)
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

        binding.etTrayQuantityError.visibility = View.GONE


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

        val tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
        val peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0
        val totalTrayQty = tray + (peti * traysPerPeti)

        // 1️⃣ No stock remaining
        if (remainingQty <= 0) {
            Toast.makeText(this, "No stock remaining", Toast.LENGTH_SHORT).show()
            valid = false
        }


        if (totalTrayQty > remainingQty) {
            Toast.makeText(this, "Not enough stock available", Toast.LENGTH_SHORT).show()
            valid = false
        }
        if (tray > 11) {
            binding.etTrayQuantityError.visibility = View.VISIBLE
            binding.etTrayQuantityError.text = "Cannot exceed 11 trays"
            valid = false
        }

        if (tray == 0 && peti == 0) {
            binding.etTrayQuantityError.visibility = View.VISIBLE
            binding.etTrayQuantityError.text = "Please enter tray or peti quantity"
            valid = false
        }

        // 3️⃣ Remaining stock check
//        if (totalTrayQty > remainingQty) {
//            binding.etTrayQuantityError.visibility = View.VISIBLE
//            binding.etTrayQuantityError.text = "Not enough trays available"
//            valid = false
//        }

        return valid
    }

    private fun submitSale() {
        val tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
        val peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0

        val totalTrayQty = tray + (peti * traysPerPeti)

        if (totalTrayQty <= 0) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = trayProductObj?.id ?: 0



        val gson = Gson()

        val itemsList = listOf(
            mapOf(
                "product_id" to productId,
                "qty" to totalTrayQty
            )
        )

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
//            "liquid" to mapOf(
////                "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
////                "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
//                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
//            )

            "liquid" to mapOf(
                "kg" to (binding.etLiquidKg.text.toString().toIntOrNull() ?: 0)
            )
        )

        val damageEggsJson = gson.toJson(damageEggsMap)
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
                    Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }

                Status.LOADING -> { /* Already handled */ }
            }
        }
    }

    private fun submitSaleCheque() {

        val tray = binding.etTrayQuantity.text.toString().toIntOrNull() ?: 0
        val peti = binding.etPetiQuantity.text.toString().toIntOrNull() ?: 0

        val totalTrayQty = tray + (peti * traysPerPeti)

        if (totalTrayQty <= 0) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = trayProductObj?.id ?: 0



        val gson = Gson()

        val itemsList = listOf(
            mapOf(
                "product_id" to productId,
                "qty" to totalTrayQty
            )
        )

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
//            "liquid" to mapOf(
//               // "peti" to (binding.etLiquidPeti.text.toString().toIntOrNull() ?: 0),
//             //   "tray" to (binding.etLiquidTray.text.toString().toIntOrNull() ?: 0),
//                "single" to (binding.etLiquidSingle.text.toString().toIntOrNull() ?: 0)
//            )

            "liquid" to mapOf(
                "kg" to (binding.etLiquidKg.text.toString().toIntOrNull() ?: 0)
            )
        )

        val damageEggsJson = gson.toJson(damageEggsMap)
        val damageEggsBody = damageEggsJson.toRequestBody("application/json".toMediaTypeOrNull())

        // ---------------- BASIC FIELDS ----------------
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

