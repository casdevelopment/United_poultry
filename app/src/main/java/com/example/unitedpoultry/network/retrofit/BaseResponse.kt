package com.example.unitedpoultry.network.retrofit

data class BaseResponse<T>(
    val result: String,
    val message: String,
    val data: T?,
    val token: String? = null
)
