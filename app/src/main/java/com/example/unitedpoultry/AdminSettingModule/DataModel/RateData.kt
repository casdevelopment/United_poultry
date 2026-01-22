package com.example.unitedpoultry.AdminSettingModule.DataModel

import com.example.unitedpoultry.AdminRiderModule.model.Pagination
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel

data class RateData(
    val history:  List<HistoryData>?,
    val pagination:  PaginationData?=null,
    val date:  String ?=null,
    val rates:  List<TodayRateItem>?,

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
data class PaginationData(
    val last_page:  Int ,
    val current_page:  Int ,
    val total_matching_record:  Int ,

)
data class TodayRateItem(
    var product_id:  Int=0,
    var product_name:  String ?=null,
    var packing:  String ?=null,
    var eggs_count:  Int=0,
    var price:  Int=0,

    )

data class UpdateRateModel(
    var date:  String,
    var rates:   List<UpdateRateItem>? ?=null,

    ){
    // Optional secondary constructor for empty initialization
    constructor() : this("", null)
}
data class UpdateRateItem(
    var product_id:  Int=0,
    var price:  Int=0,

    )
