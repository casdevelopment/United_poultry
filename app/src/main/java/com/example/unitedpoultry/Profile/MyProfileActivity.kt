package com.example.unitedpoultry.Profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.databinding.ActivityMyProfileBinding

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val balance = intent.getStringExtra("balance") ?: "Unknown"

//        binding.tvShopName.text = name
//        binding.tvShopAddress.text = address
//        binding.tvInitials.text = getInitials(name)
//        binding.tvBalance.text = "Rs. $balance"
//
//        binding.btnConfirmCollection.setOnClickListener {
//            val intent = Intent(this, CollectionSuccessActivity::class.java)
//            intent.putExtra("name", name)
//            intent.putExtra("address", address)
//            intent.putExtra("initials",  getInitials(name))
//            startActivity(intent)
//
//        }


//        binding.cardCheque.setOnClickListener {
//            val intent = Intent(this, ChequeDetailsActivity::class.java)
//            intent.putExtra("name", name)
//            intent.putExtra("address", address)
//            intent.putExtra("initials",  getInitials(name))
//            intent.putExtra("balance", balance)
//            startActivity(intent)
//
//        }


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
