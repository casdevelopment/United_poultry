package com.example.unitedpoultry.AdminArea.model

data class AddAreaRequestModel (

    val name : String,
    val city : String?,
    val description : String?,
    val is_active : Boolean
)