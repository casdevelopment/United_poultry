package com.example.unitedpoultry.History.model



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
    val payment_type: String?,
    val collection_amount: Int,
    val borrowed_amount: Int,
    val payment_record_url: String?,
    val payment_note: String?,
    val sale_date: String,
    val items: List<SaleHistoryItem>,
    val damage_eggs: DamageEggs
)







data class SaleHistoryItem(
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
    val expire: EggStatus,
    val `return`: EggStatus,
    val liquid: EggStatus
)

data class EggStatus(
    val peti: Int,
    val tray: Int,
    val single: Int,
    val total_eggs: Int
)


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