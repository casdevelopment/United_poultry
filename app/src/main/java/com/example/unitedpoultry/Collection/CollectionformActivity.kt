package com.example.unitedpoultry.Collection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.databinding.ActivityCollectionformBinding

class CollectionformActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionformBinding
    private lateinit var adapter: SelectShopAdapter


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

        val name = intent.getStringExtra("NAME") ?: "N/A"
        val address = intent.getStringExtra("ADDRESS")?: "N/A"
        val balance = intent.getStringExtra("balance") ?: "Unknown"

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = getInitials(name)
        binding.tvBalance.text = "Rs. $balance"

        binding.btnConfirmCollection.setOnClickListener {
            val intent = Intent(this, CollectionSuccessActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials",  getInitials(name))
            startActivity(intent)

        }


        binding.cardCheque.setOnClickListener {
            val intent = Intent(this, ChequeDetailsActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials",  getInitials(name))
            intent.putExtra("balance", balance)
            startActivity(intent)

        }


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
