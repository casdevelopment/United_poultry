package com.example.unitedpoultry.Profile.model



data class ChangePasswordRequestModel(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String

)