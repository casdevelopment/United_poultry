package com.example.unitedpoultry.AdminSettingModule.DataModel

import com.example.unitedpoultry.AdminRiderModule.model.RiderModel

data class RateData(
    val history:  List<HistoryData>?

)
data class HistoryData(
    val date:  String ,
    val rates:  List<RatesData>?

)
data class RatesData(
    val id:  String ,
    val product_id:  String ,
    val product_name:  String ,
    val packing:  String ,
    val eggs_count:  String ,
    val price:  String ,
    val date:  String ,


)
