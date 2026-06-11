package com.example.unitedpoultry.History.model

import com.google.gson.annotations.SerializedName


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



data class SaleHistoryData(
    val sales: List<SaleItem>,
    val pagination: Pagination
)






//
//data class HistoryResponse(
//    val result: String,
//    val message: String,
//    val data: HistoryData
//)

data class HistoryData(
    val summary: Summary,
    val product_status: ProductStatus,
    val filter_date: String,
    val filter_date_label: String,
    val transaction_type_filter: String,
    val transactions: List<TransactionItem>,
    val pagination: Pagination
)

data class Summary(
    val total_cash_in: Int,
    val total_repaid: Int,
    val borrowed: Int,
    val total_expenses: Int,
    val transactions_count: Int
)

data class ProductStatus(
    val expire: Map<String, Int>,
    val `return`: Map<String, Int>,
    val liquid: ProductLiquidDetail
)


data class ProductLiquidDetail(
    val kg: Double,
)

data class TransactionItem(
    val id: Int,
    val transaction_type: String,
    val shop_id: Int,
    val shop_name: String,
    val description: String,
    val amount: Int,
    val payment_type: String,
    val transaction_at: String
)

data class Pagination(
    val last_page: Int,
    val current_page: Int,
    val total_matching_record: Int
)

