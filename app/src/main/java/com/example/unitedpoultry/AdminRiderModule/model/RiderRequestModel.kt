package com.example.unitedpoultry.AdminRiderModule.model

data class RiderRequestModel(

    val name: String,
    val email: String,
    val username: String,
    val phone_number: String,
    val cnic: String,
    val address: String,
    val password: String,
    val is_active: Boolean,

)