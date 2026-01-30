package com.example.unitedpoultry.AdminSettingModule

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.AdminSettingModule.adapter.CatagoryAdapter
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityAdminCatagoryBinding
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.Status.*
import com.example.unitedpoultry.util.AppUtil
import com.example.unitedpoultry.util.showToast
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminCatagoryActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminCatagoryBinding
    private val viewModel: AdminSettingViewModel by viewModel()

    private val todayRateList = mutableListOf<TodayRateItem>()
    private lateinit var catagoryAdapter: CatagoryAdapter

    private lateinit var fields: HashMap<Any, Any>
    private val viewModel1: AdminSettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminCatagoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        setupRecyclerView()

        onclick()



    }

    override fun onResume() {
        super.onResume()
        getTodayRate()
    }

    private fun  onclick(){

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

            val dialog = AlertDialog.Builder(this@AdminCatagoryActivity)
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

    private fun setupRecyclerView() {
        catagoryAdapter = CatagoryAdapter(todayRateList)
        binding.todatRateRv.apply {
            layoutManager =
                LinearLayoutManager(this@AdminCatagoryActivity, RecyclerView.VERTICAL, false)
            adapter = catagoryAdapter
        }
    }

    private fun getTodayRate() {
        viewModel.todayRate().observe(this) { response ->
            when (response.status) {

                SUCCESS -> {
                    AppUtil.stopLoader()

                    if (response.data?.isSuccessful == true) {
                        val baseResponse = response.data.body()

                        // Date
                        binding.rateDate.text =
                            baseResponse?.data?.date ?: ""

                        // List
                        todayRateList.clear()
                        baseResponse?.data?.rates?.let {
                            todayRateList.addAll(it)
                        }

                        catagoryAdapter.notifyDataSetChanged()
                    }
                }

                ERROR -> {
                    AppUtil.stopLoader()
                    showToast(response.message ?: "Something went wrong")
                }

                LOADING -> {
                    AppUtil.startLoader(this)
                }
            }
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

        viewModel1.createProduct(fields).observe(this@AdminCatagoryActivity) { serverResponse ->

            when (serverResponse.status) {

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    if (serverResponse.data?.isSuccessful == true) {
                        val message =
                            serverResponse.data.body()?.message ?: "Product created successfully"

                        showToast(message)

                        // Refresh list after adding product
                        getTodayRate()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    showToast(serverResponse.message ?: "Not able to save the product")
                }

                Status.LOADING -> {
                    AppUtil.startLoader(this@AdminCatagoryActivity)
                }
            }
        }
    }





}
