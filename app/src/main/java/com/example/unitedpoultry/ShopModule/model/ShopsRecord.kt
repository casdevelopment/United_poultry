package com.example.unitedpoultry.ShopModule.model

data class ShopsRecord(
    val name: String,
    val address: String,
    val status: String,
    val lastVisit: String,
    val rate: Int,
    val dues: Int,
)
