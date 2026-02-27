package com.example.unitedpoultry.rider_expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ExpenseViewModel(private val repository: Repository) : ViewModel() {

    fun addExpenseByCash(
        title: RequestBody,
        amount: RequestBody,
        note: RequestBody? = null,
        payment_type: RequestBody,
        expense_date: RequestBody
    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addExpenseByCash(title, amount, note, payment_type,expense_date)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }


    fun addExpenseByCheque(
        title: RequestBody,
        amount: RequestBody,
        note: RequestBody? = null,
        payment_type: RequestBody,
        expense_date: RequestBody,
        payment_record: MultipartBody.Part,
        payment_note: RequestBody? = null,

    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addExpenseByCheque(title, amount, note, payment_type,expense_date,payment_record,payment_note)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
