package com.example.unitedpoultry.NewSale.model

data class SaleResponse(
    val id: Int,
    val seller_id: Int,
    val shop_id: String,
    val shop_name: String,
    val area_id: String,
    val area_name: String,
    val sub_total: Double,
    val discount: Double,
    val total: Double,
    val cash_received: Double,
    val payment_type: String,
    val collection_amount: Double,
    val borrowed_amount: Double,
    val payment_record_url: String?,
    val payment_note: String?,
    val sale_date: String,
    val items: List<Sale>
   // val damage_eggs: DamageEggsResponse
)



data class Sale(
    val id: Int,
    val product_id: Int,
    val product_name: String,
    val packing: String,
    val eggs_count: Int,
    val total_eggs: Int,
    val qty: Int,
    val price: String,
    val line_total: String
)







