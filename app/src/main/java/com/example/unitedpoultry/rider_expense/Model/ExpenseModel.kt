package com.example.unitedpoultry.rider_expense.Model

data class ExpenseModel(
    val id: Int,
    val title: String,
    val amount: Int,
    val note: String?,
    val payment_type: String,
    val payment_record_url: String?,
    val payment_note: String?,
    val expense_date: String,
    val created_at: String
)