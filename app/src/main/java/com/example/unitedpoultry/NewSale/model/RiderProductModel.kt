package com.example.unitedpoultry.NewSale.model

import com.google.gson.annotations.SerializedName

data class Product(
    val product_id: Int,
    val product_name: String,
    val quantity: Int,
    val remaining_quantity: Int,
    val eggs_count: Int,
    val total_eggs: Int,
    val price: String



)

data class RiderProductData(
    val date: String,
    val picked_items: List<Product>
)











data class SaleProduct(
    val id: Int,
    val name: String,
    val remainingQty: Int,
    val price: Double
)

data class RiderProductUI(
    val id: Int,
    val name: String,
    val price: Double,
    val remainingQty: Int
)




data class DamageEggsRequest(
    val damage_eggs: Damage
)

data class Damage(
    val expire: List<QtyRequest>,
    @SerializedName("return")
    val returnData: List<QtyRequest>,

    val liquid: LiquidItem
)


data class LiquidItem(
    val kg: Double,

    )

data class QtyRequest(
    val product_id: Int,
    val qty: Int
)