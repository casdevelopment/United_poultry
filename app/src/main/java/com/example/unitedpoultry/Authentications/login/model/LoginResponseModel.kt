package com.example.unitedpoultry.Authentications.login.model

data class LoginResponseModel(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?, // nullable
    val phone_number: String,
    val username : String,
    val address: String,
    val cnic: String?,
    val image: String?,
    val is_active: Boolean,
    val role_id: Int,
    val created_at: String,
    val updated_at: String
)
