package com.example.unitedpoultry.History.model

data class HistoryModel(
    val name: String,
    val description: String,
    val type: String,   // Sale / Collection
    val amount: Int,
    val day: Int,       // days ago (0 = today)
    val time: String
)
