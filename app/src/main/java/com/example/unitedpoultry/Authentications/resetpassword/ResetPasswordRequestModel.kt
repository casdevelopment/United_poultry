package com.example.unitedpoultry.Authentications.resetpassword

data class ResetPasswordRequestModel (
    val email: String,
    val otp: String,
    val password: String,
    val password_confirmation: String,
)
