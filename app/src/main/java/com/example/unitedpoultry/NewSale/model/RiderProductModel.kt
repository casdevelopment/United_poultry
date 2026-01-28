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
