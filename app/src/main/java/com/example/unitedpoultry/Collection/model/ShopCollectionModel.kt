package com.example.unitedpoultry.Collection.model

data class ShopCollectionModel(
    val name: String,
    val address: String,
    val due: Int
)


data class CollectionResponse(
    val id: Int,
    val receipt_number: String,
    val shop_id: Int,
    val shop_name: String,
    val shop_address: String,
    val amount_collected: Int,
    val payment_type: String,
    val payment_note: String?,
    val payment_record_url: String?,
    val previous_balance: String,
    val remaining_balance: String,
    val collected_at: String,
    val seller_id: Int
)