package com.example.unitedpoultry.AdminShopModule.model

data class Area(
    val id: Int,
    val name: String,
    val description: String,
    val city: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)


data class ShopModel(
    val id: Int,
    val name: String,
    val address: String,
    val discount_per_petti: String,
    val area_id: Int,
    val area_name: String,

    val cash_in: Double,
    val borrowed: Double,
    val repaid: Double,

    val visited: Boolean,

    val contact_person: String?,
    val phone_number: String?,
    val image: String?,
    val is_active: Boolean?
)


data class ShopsPagination(
    val last_page: Int,
    val current_page: Int,
    val total_matching_record: Int
)

data class ShopsData(
    val area_id: Int,
    val area_name: String,
    val total_shops_assigned: Int,
    val filter_counts: FilterCounts,
    val shops: List<ShopModel>,
    val pagination: ShopsPagination
)

data class FilterCounts(
    val all: Int,
    val visited: Int,
    val pending: Int
)

