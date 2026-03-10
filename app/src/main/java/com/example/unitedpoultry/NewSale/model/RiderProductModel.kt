package com.example.unitedpoultry.NewSale.model

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
    val total_picked: Quantity,
    val remaining: Remaining,
    val categories: Categories,
    val products: List<ProductInfo>
)

data class Quantity(
    val peti: Int,
    val tray: Int
)

data class Remaining(
    val total_peti: Int,
    val total_trays: Int
)



data class Categories(
    val expire: CategoryItem,
    val `return`: CategoryItem,
    val liquid: CategoryItem
)

data class CategoryItem(
    val peti: Int,
    val tray: Int,
    val single: Int
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


data class SaleProductTray(
    val id: Int,
    val name: String,
    val latest_price: Double,
    var total_trays: Int = 0
)