package com.example.unitedpoultry.AdminShopModule.model

import com.example.unitedpoultry.AdminArea.model.AreaModel

data class ShopDetailsResponseModel(
    val id: Int,
    val name: String,
    val contact_person: String,
    val phone_number: String,
    val area_id: Int,
    val address: String,
    val image: String?,
    val discount_per_petti: String,
    val is_active: Boolean,
    val area: AreaModel?
)
