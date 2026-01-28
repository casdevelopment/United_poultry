package com.example.unitedpoultry.NewSale.model

data class SaleItem(
    val product_id: Int,
    val qty: Int
)

data class SaleRequest(
    val shop_id: Int,
    val area_id: Int,
    val sub_total: Double,
    val discount: Double,
    val total: Double,
    val cash_received: Double,
    val items: List<SaleItem>
)



