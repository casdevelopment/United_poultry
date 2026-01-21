package com.example.unitedpoultry.rider_home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class EggPickupViewModel(private val repository: Repository) : ViewModel() {

    fun savePickedEggs(request: EggPickupRequest, token: String):
            LiveData<NetworkStates<Response<BaseResponse<EggPickupData>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.savePickedEggs(request, token)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }
}

