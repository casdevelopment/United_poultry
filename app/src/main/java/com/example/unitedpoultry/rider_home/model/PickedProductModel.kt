package com.example.unitedpoultry.rider_home.model



data class PickedToday(
    val date: String,
    val total_picked: Map<String, Int>,
    val remaining: Map<String, Int>,
    val categories: PickedTodayCategories,
    val products: List<PickedTodayProduct>
)


data class PickedTodayCategories(
    val expire: Map<String, Int>,
    val `return`: Map<String, Int>,
    val liquid: PickedTodayLiquid
)

data class PickedTodayLiquid(
    val kg: Double,
    )

data class PickedTodayProduct(
    val id: Int,
    val name: String,
    val latest_price: Double
)