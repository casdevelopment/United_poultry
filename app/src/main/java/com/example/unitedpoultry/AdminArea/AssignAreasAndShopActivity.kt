package com.example.unitedpoultry.AdminArea

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminExpenseBinding
import com.example.unitedpoultry.databinding.ActivityAssignAreasAndShopBinding

class AssignAreasAndShopActivity : BaseActivity() {

    private lateinit var binding: ActivityAssignAreasAndShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignAreasAndShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

    }
}