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



// Response for GET /seller/expenses/heads
data class ExpenseHeadResponse(
    val result: String,
    val message: String,
    val data: ExpenseHeadData?
)

data class ExpenseHeadData(
    val heads: List<ExpenseHead>
)

data class ExpenseHead(
    val id: Int,
    val name: String
)

// Request body for posting expenses
data class ExpenseItemRequest(
    val expense_id: Int,
    val amount: Double
)

data class AddExpenseRequest(
    val expenses: List<ExpenseItemRequest>,
    val total_amount: Double,
    val payment_type: String,
    val expense_date: String,
    val note: String
)