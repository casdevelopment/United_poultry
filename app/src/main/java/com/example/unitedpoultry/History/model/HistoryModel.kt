package com.example.unitedpoultry.History.model


data class SaleItem(
    val id: Int,
    val seller_id: Int,
    val shop_id: Int,
    val shop_name: String,
    val area_id: Int,
    val area_name: String,
    val sub_total: String,
    val discount: String,
    val total: String,
    val cash_received: String,
    val sale_date: String
)

data class Pagination(
    val last_page: Int,
    val current_page: Int,
    val total_matching_record: Int
)

data class SaleHistoryData(
    val sales: List<SaleItem>,
    val pagination: Pagination
)
