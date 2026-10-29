package com.example.unitedpoultry.RiderDashBoard

data class EggReturnModel(
    var date: String?=null,
    var sabut: EggType?=null,
    var mela: EggType?=null,
    var tray: EggType?=null,
    var liquid: EggType?=null,
)
data class EggType(
    var peti: String?=null,
    var tray: String?=null,
)
