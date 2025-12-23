package com.example.unitedpoultry.Collection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.databinding.ActivityChequeDetailsBinding
import com.example.unitedpoultry.databinding.ActivityCollectionformBinding
import com.example.unitedpoultry.databinding.ActivitySaleFormBinding

class ChequeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChequeDetailsBinding
    private lateinit var adapter: SelectShopAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChequeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }
        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val balance = intent.getStringExtra("balance") ?: "Unknown"
        val initials = intent.getStringExtra("initials") ?: "Unknown"

        binding.tvBalance.text = "Rs. $balance"

        binding.btnConfirmCollection.setOnClickListener {
            val intent = Intent(this, CollectionSuccessActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            intent.putExtra("initials",  initials)
            startActivity(intent)

        }



    }
}
