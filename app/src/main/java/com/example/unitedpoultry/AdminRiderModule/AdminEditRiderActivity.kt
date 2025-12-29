package com.example.unitedpoultry.AdminRiderModule


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminRiderModule.Adapter.RiderAssignedAreasAdapter
import com.example.unitedpoultry.AdminRiderModule.model.AdminAssignedAreasModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.databinding.ActivityAdminEditRiderBinding



class AdminEditRiderActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminEditRiderBinding
    private lateinit var adapter: RiderAssignedAreasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditRiderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )


        binding.backArrow.setOnClickListener { finish() }


        val name = intent.getStringExtra("RIDER_NAME") ?: "N/A"

        binding.etFullName.setText(name)

        binding.etPhoneNumber.setText("0345678623")

        binding.etEmail.setText("Ahmed@gmail.com")

        binding.etCnic.setText("34567-9732786-0")

        binding.etEmail.setText("Ahmed@gmail.com")

                binding.etname.setText(name)


    }
}
