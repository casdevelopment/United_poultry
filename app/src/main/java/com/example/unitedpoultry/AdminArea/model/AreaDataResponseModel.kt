package com.example.unitedpoultry.AdminArea.model

// AreaModel.kt
data class AreaModel(
    val id: Int,
    val name: String,
    val description: String?,
    val city: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String,
    val shops_count: Int,
    val visited: Int,
    val pending:Int

)

// PaginationModel.kt
data class PaginationModel(
    val last_page: Int,
    val current_page: Int,
    val total_matching_record: Int
)

// AreaDataResponse.kt
data class AreaDataResponseModel(
    val areas: List<AreaModel>,
    val pagination: PaginationModel
)
