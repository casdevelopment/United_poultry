package com.example.unitedpoultry.waste_return.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.waste_return.model.RiderWasteRequest
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class RiderWasteProductViewModel(private val repository: Repository) : ViewModel() {

    fun RiderWasteProduct(request: RiderWasteRequest): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.RiderWasteProduct(request)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }
}

