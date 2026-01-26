package com.example.unitedpoultry.AdminSettingModule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminLogoutViewModel
import com.example.unitedpoultry.AdminShopModule.AdminEditShopActivity
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.Splash.SplashActivity
import com.example.unitedpoultry.databinding.ActivitySettingHomeBinding
import com.example.unitedpoultry.util.AppConstants.userData
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.set


class SettingHomeActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingHomeBinding
    private lateinit var fields: HashMap<Any, Any>

    private val sessionManager: SessionManager by inject()
    private val viewModel: AdminSettingViewModel by viewModel()
    private val viewModel1: AdminLogoutViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivitySettingHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = false, // false = white icons
            colorResId = R.color.primary
        )

        showData()
        onclick()





//        binding.printerSettings.setOnClickListener {
//
//            val intent = Intent(this, AdminPrinterSettingActivity::class.java)
//            startActivity(intent)
//        }

//        binding.appAndAbout.setOnClickListener {
//
//            val intent = Intent(this, AdminAboutAndHelpActivity::class.java)
//            startActivity(intent)
//        }





//        binding.backArrow.setOnClickListener {
//            finish()
//        }

    }

    private fun showData(){

        binding.tvName.text = userData?.name ?: "User Name"
        binding.tvEmail.text = userData?.email ?: "Email"

        binding.tvInitials.text = getInitials(userData?.name)

    }

    private fun  onclick(){

//        binding.backArrow.setOnClickListener {
//            finish()
//        }

        binding.logoutCard.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
            val btnLogout = dialogView.findViewById<MaterialButton>(R.id.btnLogout)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnLogout.setOnClickListener {

                dialog.dismiss()

                callLogoutApi()

            }

            dialog.show()
        }




        binding.editProfile.setOnClickListener {

            val intent = Intent(this, AdminEditProfileActivity::class.java)
            intent.putExtra("NAME",  userData?.name)
            intent.putExtra("EMAIL", userData?.email)
            intent.putExtra("PHONE_NUMBER", userData?.phone_number)
            intent.putExtra("USERNAME", userData?.username)
            intent.putExtra("ADDRESS", userData?.address)
            intent.putExtra("INITIALS", getInitials(userData?.name))
            startActivity(intent)

        }

        binding.rateManagement.setOnClickListener {

            val intent = Intent(this, AdminRateManagmentActivity::class.java)
            startActivity(intent)
        }

        binding.productManagement.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

            val dialog = AlertDialog.Builder(this@SettingHomeActivity)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // 🔥 THIS LINE FIXES THE EDGES
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
            val btnSave = dialogView.findViewById<MaterialButton>(R.id.btnSave)
            val etName =
                dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etName)
            val etPacking =
                dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPacking)
            val etEggsCount =
                dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEggsCount)
            val etPrice =
                dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPrice)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                val name = etName.text.toString().trim()
                val packing = etPacking.text.toString().trim()
                val eggsCount = etEggsCount.text.toString().toIntOrNull() ?: 0
                val price = etPrice.text.toString().toIntOrNull() ?: 0

                if (validateInput(name, packing, eggsCount, price)) {
                    fields = HashMap()
                    fields["name"] = name
                    fields["packing"] = packing
                    fields["eggs_count"] = eggsCount.toString()
                    fields["price"] = price.toString()
                    fields["is_active"] = true
                    saveProduct(fields)
                    dialog.dismiss()

                } else {
                    showToast("Please fill all fields correctly")

                }


            }

            dialog.show()
        }
    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return "U" // Default initial
        val words = name.trim().split(" ")
        return when {
            words.size >= 2 -> "${words[0][0]}${words[1][0]}".uppercase()
            words.isNotEmpty() -> "${words[0][0]}".uppercase()
            else -> "U"
        }
    }




    private fun validateInput(
        name: String,
        packing: String,
        eggsCount: Int,
        price: Int
    ): Boolean {
        return name.isNotEmpty() &&
                packing.isNotEmpty() &&
                eggsCount > 0 &&
                price > 0
    }

    private fun saveProduct(fields: HashMap<Any, Any>) {

        viewModel.createProduct(fields).observe(this@SettingHomeActivity) { serverResponse ->

            when (serverResponse.status) {

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    showToast(serverResponse.message.toString())
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    showToast("Not able to save the product")
                }

                Status.LOADING -> {
                    AppUtil.startLoader(this@SettingHomeActivity)
                }
            }

        }
    }


    private fun callLogoutApi() {

        viewModel1.adminLogout().observe(this) { response ->

            when (response.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val body = response.data?.body()

                    if (body?.result == "success") {

                        sessionManager.logout()

                        val intent = Intent(this, SplashActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        showToast(body?.message ?: "Logout failed")
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    showToast("Logout failed. Please try again")
                }
            }
        }
    }


}