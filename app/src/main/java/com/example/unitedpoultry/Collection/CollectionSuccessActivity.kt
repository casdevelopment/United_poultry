package com.example.unitedpoultry.Collection


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Collection.model.CollectionResponse
import com.example.unitedpoultry.History.FullScreenImageActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.RotateTransformation
import com.example.unitedpoultry.databinding.ActivityCollectionSuccessBinding
import com.example.unitedpoultry.databinding.ActivitySaleSuccessBinding
import com.example.unitedpoultry.util.AppConstants
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class CollectionSuccessActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionSuccessBinding

    private var fullImageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        binding.ivPaymentSlip.setOnClickListener {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", fullImageUrl) // pass your image URL or local path
            startActivity(intent)
        }


        val jsonData = intent.getStringExtra("collection_data_json")
        val collectionData = Gson().fromJson(jsonData, CollectionResponse::class.java)

        binding.tvSubTitle.text = "Receipt #${collectionData.receipt_number} has been generated"

        binding.tvShopName.text = collectionData.shop_name
        binding.tvShopAddress.text = collectionData.shop_address
        binding.tvInitials.text = getInitials(collectionData.shop_name)

        binding.tvPreviousBalance.text = "Rs ${collectionData.previous_balance}"


        binding.tvAmountCollected.text = "Rs ${collectionData.amount_collected}"

        binding.tvPaymentType.text = collectionData.payment_type

        binding.tvCollectedAt.text = formatDateTime(collectionData.collected_at)

        binding.tvRemainingBalance.text = "Rs ${collectionData.remaining_balance}"


        val imageUrl = collectionData.payment_record_url
        if (!imageUrl.isNullOrEmpty()) {
            binding.slipLayout.visibility = View.VISIBLE

            fullImageUrl = AppConstants.ImageURL + imageUrl

            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(binding.ivPaymentSlip.drawable)
                .into(binding.ivPaymentSlip)

        }

        if (collectionData.payment_note != null) {
            binding.noteLayout.visibility = View.VISIBLE
            binding.etNote1.text = collectionData.payment_note

        }



        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }


    }

    private fun getInitials(name: String): String {
        if (name.isBlank()) return ""
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else parts[0][0].uppercase()
    }

    fun formatDateTime(dateTimeString: String): String {
        return try {
            // Input format from server
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)

            // Desired output format
            val outputFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateTimeString
        } catch (e: Exception) {
            dateTimeString // fallback if parsing fails
        }
    }

    private fun finishActivity() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishActivity()
    }
}
