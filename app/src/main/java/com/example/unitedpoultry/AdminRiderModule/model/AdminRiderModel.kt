package com.example.unitedpoultry.AdminRiderModule.model

data class AdminRiderModel(
    val name: String,
    val joiningDate: String,
    val status: String,
    val areas: Int,
    val shops: Int,
    val todaySale: Int
)