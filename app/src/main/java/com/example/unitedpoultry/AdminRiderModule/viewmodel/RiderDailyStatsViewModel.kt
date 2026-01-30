package com.example.unitedpoultry.AdminRiderModule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class RiderDailyStatsViewModel(private val repository: Repository) : ViewModel() {

    fun getRiderDailyStats(id : Int): LiveData<NetworkStates<Response<BaseResponse<EggPickupData>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getRiderDailyStats(id) // ✅ no token
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }

}

