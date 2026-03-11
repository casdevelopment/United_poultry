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

data class ShopDetailsResponse(
    val id: Int,
    val name: String,
    val address: String,
    val area_id: Int,
    val area_name: String,
    val image: String,
    val cash_in: Int,
    val borrowed: Int,
    val discount_per_petti: Double,
    val repaid: Int,
    val owner_name: String,
    val phone_number: String,
    val last_visit: String,
    val damage_return: DamageReturn
)

data class DamageReturn(
    val expire: DamageItem,
    val `return`: DamageItem,
    val liquid: DamageLiquidItem
)

data class DamageItem(
    val peti: Int,
    val tray: Int,
    val single: Int
)

data class DamageLiquidItem(
    val kg: Int,
)
