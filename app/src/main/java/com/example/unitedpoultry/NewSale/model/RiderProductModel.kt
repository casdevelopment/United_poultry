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


data class PickedItemsResponse(
    val date: String,
    val total_picked: Map<String, Int>,
    val remaining: Map<String, Int>,
    val categories: Categories,
    val products: List<ProductInfo>
)

data class Quantity(
    val peti: Int,
    val tray: Int
)

data class Remaining(
    val name: String,
    val quantity: Int,
)



data class Categories(
    val expire: CategoryItem,
    val `return`: CategoryItem,
    val liquid: LiquidItem
)

data class CategoryItem(
    val peti: Int,
    val tray: Int,
    val single: Int
)

data class LiquidItem(
    val kg: Int,

)

data class ProductInfo(
    val id: Int,
    val name: String,
    val latest_price: Double
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

data class SaleProductTray(
    val id: Int,
    val name: String,
    val latest_price: Double,
    var total_trays: Int = 0
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

data class QtyRequest(
    val product_id: Int,
    val qty: Int
)