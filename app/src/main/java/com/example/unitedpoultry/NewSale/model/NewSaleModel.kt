package com.example.unitedpoultry.NewSale.model

import com.google.gson.annotations.SerializedName

//data class SaleItem(
//    val product_id: Int,
//    val qty: Int
//)

data class SaleRequest(
    val shop_id: Int,
    val area_id: Int,
    val sub_total: Double,
    val discount: Double,
    val total: Double,
    val cash_received: Double,
    val items: List<SaleItem>
)





data class SaleItem(
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

data class EggCount(
    val peti: Int,
    val tray: Int,
    val single: Int,
    val total_eggs: Int
)


data class DamageEggs(
    val expire: EggCount,

    @SerializedName("return")
    val return_: EggCount,

    val liquid: EggCount
)

data class NewSaleRequest(
    val shop_id: Int,
    val area_id: Int,
    val payment_type: String,
    val collection_amount: Double,
    val borrowed_amount: Double,
    val items: List<SaleItem>,
    val damage_eggs: DamageEggs
)

data class SaleResponseData(
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
    val items: List<SaleItem>,
    val damage_eggs: DamageEggs
)


