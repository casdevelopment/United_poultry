package com.example.unitedpoultry.Collection.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.Collection.model.CollectionResponse
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class CollectionViewModel(private val repository: Repository) : ViewModel() {

    fun addCollectionByCash(
        shop_id: RequestBody,
        payment_type: RequestBody,
        amount: RequestBody
    ): LiveData<NetworkStates<Response<BaseResponse<CollectionResponse>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addCollectionByCash(shop_id, payment_type, amount)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }


    fun addCollectionByCheque(
        shop_id: RequestBody,
        payment_type: RequestBody,
        amount: RequestBody,

        payment_record: MultipartBody.Part,
        payment_note: RequestBody? = null,

        ): LiveData<NetworkStates<Response<BaseResponse<CollectionResponse>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addCollectionByCheque(shop_id, payment_type, amount, payment_record,payment_note)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
