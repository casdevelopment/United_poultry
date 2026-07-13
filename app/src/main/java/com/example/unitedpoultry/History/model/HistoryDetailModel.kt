package com.example.unitedpoultry.History.model



data class CollectionHistory(
    val id: Int,
    val receipt_number: String,
    val shop_id: Int,
    val shop_name: String,
    val shop_address: String,
    val amount_collected: Int,
    val payment_type: String,
    val payment_note: String?,
    val payment_record_url: String?,
    val previous_balance: Int,
    val remaining_balance: Int,
    val collected_at: String,
    val seller_id: Int
)

data class ExpenseItem(
    val id: Int,
    val title: String,
    val amount: Int,
    val note: String?,
    val payment_type: String,
    val payment_record_url: String?,
    val payment_note: String?,
    val expense_date: String,
    val created_at: String
)




data class SaleHistory(
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

    val payment_type: String,

    val collection_amount: Double,
    val borrowed_amount: Double,

    val payment_record_url: String?,
    val payment_note: String?,

    val sale_date: String,

    val items: List<SaleItems>,
    val damage_eggs: DamageEggs
)



data class SaleItems(
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


data class DamageEggs(
    val expire: List<DamageItems>,
    val `return`: List<DamageItems>,
    val liquid: Liquid
)

data class DamageItems(
    val product_id: Int,
    val product_name: String,
    val qty: Int,
    val total_eggs: Int,
)



data class Liquid(
    val kg: Double
)