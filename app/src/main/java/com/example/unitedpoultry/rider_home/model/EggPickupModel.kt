package com.example.unitedpoultry.rider_home.model

data class PickedItem(
    val product_id: Int,
    val quantity: Int
)

data class EggPickupRequest(
    val date: String,
    val items: List<PickedItem>
)

data class PickedItemResponse(
    val product_name: String,
    val quantity: Int,
    val eggs_count: Int,
    val total_eggs: Int,
    val price: String
)

data class EggPickupData(
    val date: String,
    val total_picked: Int,
    val total_sold: Int,
    val total_returned: Int,
    val total_waste: Int,
    val picked_items: List<PickedItemResponse>
)