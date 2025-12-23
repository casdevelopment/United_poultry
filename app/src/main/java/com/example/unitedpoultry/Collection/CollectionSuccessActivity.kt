package com.example.unitedpoultry.Collection


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unitedpoultry.NewSale.Adapter.SelectShopAdapter
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivityCollectionSuccessBinding
import com.example.unitedpoultry.databinding.ActivitySaleSuccessBinding


class CollectionSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionSuccessBinding
    private lateinit var adapter: SelectShopAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val initials = intent.getStringExtra("initials") ?: "Unknown"

        binding.tvShopName.text = name
        binding.tvShopAddress.text = address
        binding.tvInitials.text = initials


        binding.btnNewCollection.setOnClickListener {
            val intent = Intent(this, SelectShopCollectionActivity::class.java)
            startActivity(intent)

        }

        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            startActivity(intent)

        }


    }
}
