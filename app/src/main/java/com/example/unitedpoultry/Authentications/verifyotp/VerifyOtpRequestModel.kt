package com.example.unitedpoultry.Authentications.verifyotp

data class VerifyOtpRequestModel (

    val email: String,
    val otp: String
)