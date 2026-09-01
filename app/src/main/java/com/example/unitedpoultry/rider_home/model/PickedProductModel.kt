package com.example.unitedpoultry.rider_home.model



data class PickedToday(
    val date: String,
    val total_picked: Map<String, Int>,
    val remaining: Map<String, Int>,
    val categories: PickedTodayCategories,
    val products: List<PickedTodayProduct>
)


data class PickedTodayCategories(
    val sabut: Map<String, Int>,
    val mela: Map<String, Int>,
    val toota: Map<String, Int>,
    val liquid: PickedTodayLiquid
    //val liquid: Map<String, Int>,
)

data class PickedTodayLiquid(
    val kg: Double,
    )

data class PickedTodayProduct(
    val id: Int,
    val name: String,
    val latest_price: Double
)