package com.example.unitedpoultry.rider_expense.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_expense.Model.ExpenseHeadData
import com.example.unitedpoultry.rider_expense.Model.ExpenseModel
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ExpenseViewModel(private val repository: Repository) : ViewModel() {

    fun addExpenseByCash(
        expenses: RequestBody,
        totalAmount: RequestBody,
        paymentType: RequestBody,
        expenseDate: RequestBody,
        note: RequestBody? = null
    ): LiveData<NetworkStates<Response<BaseResponse<ExpenseModel>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addExpenseByCash(expenses, totalAmount, paymentType, expenseDate, note)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }


    fun addExpenseByCheque(
        expenses: RequestBody,
        totalAmount: RequestBody,
        paymentType: RequestBody,
        expenseDate: RequestBody,
        note: RequestBody? = null,
        payment_record: MultipartBody.Part,
        payment_note: RequestBody? = null,

    ): LiveData<NetworkStates<Response<BaseResponse<ExpenseModel>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addExpenseByCheque(expenses, totalAmount, paymentType, expenseDate, note,payment_record,payment_note)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }


    fun getExpenses(): LiveData<NetworkStates<Response<BaseResponse<ExpenseHeadData>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getExpenses()
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
