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
    val product_id: Int,
    val qty: Int
)

data class EggCount(
    val peti: Int,
    val tray: Int,
    val single: Int
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


