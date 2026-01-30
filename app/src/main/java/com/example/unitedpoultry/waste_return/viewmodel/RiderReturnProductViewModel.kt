package com.example.unitedpoultry.waste_return.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.waste_return.model.RiderReturnRequest

class RiderReturnProductViewModel(private val repository: Repository) : ViewModel() {

    fun riderReturnProduct(request: RiderReturnRequest): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.RiderReturnRequest(request)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }
}
