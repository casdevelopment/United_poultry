package com.example.unitedpoultry.AdminRiderModule.model


data class RiderModel(
    val id: Int,
    val name: String,
    val email: String?,
    val username: String?,
    val cnic: String?,
    val email_verified_at: String?,
    val phone_number: String?,
    val address: String?,
    val is_active: Boolean,
    val image: String?,
    val role_id: Int,
    val password : String?,
    val created_at: String?,
    val updated_at: String?
)

data class RiderDataResponceModel(
    val sellers: List<RiderModel>?,
    val pagination: Pagination?
)

data class Pagination(
    val last_page: Int,
    val current_page: Int,
    val total_matching_record: Int
)