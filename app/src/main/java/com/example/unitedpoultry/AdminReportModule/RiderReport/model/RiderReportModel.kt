package com.example.unitedpoultry.AdminReportModule.RiderReport.model

data class RiderReportModel(
    val name: String,
    val sales: String,
    val collected: String,
    val efficiency: String,
    val areas: Int,
    val shops: Int,
    val date: String   // yyyy-MM-dd
)