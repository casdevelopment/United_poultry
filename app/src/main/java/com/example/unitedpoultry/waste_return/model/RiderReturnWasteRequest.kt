package com.example.unitedpoultry.waste_return.model

data class RiderReturnRequest(
    val date: String,
    val returned_items: List<RiderReturnItem>
)

data class RiderReturnItem(
    val product_id: Int,
    val qty: Int
)

data class RiderWasteRequest(
    val date: String,
    val waste_items: List<RiderReturnItem>
)


