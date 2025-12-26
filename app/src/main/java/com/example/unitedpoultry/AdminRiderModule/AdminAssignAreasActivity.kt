package com.example.unitedpoultry.AdminRiderModule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.unitedpoultry.AdminDashBoard.AdminDashBoardActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminAssignAreasBinding


class AdminAssignAreasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAssignAreasBinding
  //  private lateinit var adapter: RiderAssignedAreasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAssignAreasBinding.inflate(layoutInflater)
        setContentView(binding.root)

    //    binding.backArrow.setOnClickListener { finish() }


        val name = intent.getStringExtra("name") ?: "N/A"
        val initials = intent.getStringExtra("initial") ?: "N/A"


        binding.tvInitials.text = initials
        binding.tvRiderName.text = name

        setupCheckbox(binding.cb1, binding.area1)
        setupCheckbox(binding.cb2, binding.area2)
        setupCheckbox(binding.cb3, binding.area3)

        binding.btnSave.setOnClickListener {
            val intent = Intent(this, AdminDashBoardActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupCheckbox(
        checkBox: android.widget.CheckBox,
        cardView: CardView
    ) {

        // Initial color
        cardView.setCardBackgroundColor(getColor(R.color.primary20))

        // Checkbox change listener
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cardView.setCardBackgroundColor(getColor(R.color.primary))
            } else {
                cardView.setCardBackgroundColor(getColor(R.color.primary20))
            }
        }

        // Clicking card toggles checkbox
        cardView.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
        }
    }
}
