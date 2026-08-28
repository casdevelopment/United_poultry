package com.example.unitedpoultry.rider_expense.Model
import com.google.gson.annotations.SerializedName

//data class ExpenseModel(
//    val id: Int,
//    val title: String,
//    val amount: Int,
//    val note: String?,
//    val payment_type: String,
//    val payment_record_url: String?,
//    val payment_note: String?,
//    val expense_date: String,
//    val created_at: String
//)



data class ExpenseModel(

    @SerializedName("id")
    val id: Int,

    @SerializedName("expenses")
    val expenses: List<ExpenseItem>?,

    @SerializedName("total_amount")
    val totalAmount: Int?,

    @SerializedName("payment_type")
    val paymentType: String?,

    @SerializedName("expense_date")
    val expenseDate: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("payment_record_url")
    val paymentRecordUrl: String? = null,

    @SerializedName("payment_note")
    val paymentNote: String? = null,

    @SerializedName("note")
    val note: String? = null
)

data class ExpenseItem(


    @SerializedName("expense_id")
    val expenseId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("amount")
    val amount: Int
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