package com.example.unitedpoultry.exchange_return.model


data class ReturnOrExchangeRequest(
    val shop_id: Int,
    val area_id: Int,
    val items: List<ReturnExchangeItem>
)

data class ReturnExchangeItem(
    val product_id: Int,
    val qty: Int,
    val return_reason: String // "Return" or "Exchange"
)

