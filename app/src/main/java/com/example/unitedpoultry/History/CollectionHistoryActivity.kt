package com.example.unitedpoultry.History

import android.content.Intent
import android.os.Bundle
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.RiderDashBoardActivity
import com.example.unitedpoultry.databinding.ActivityCollectionHistoryBinding


class CollectionHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionHistoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false,
            colorResId = R.color.mint
        )


        onclick()



    }

    private fun onclick(){
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.moveToDashBoard.setOnClickListener {
            val intent = Intent(this, RiderDashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}