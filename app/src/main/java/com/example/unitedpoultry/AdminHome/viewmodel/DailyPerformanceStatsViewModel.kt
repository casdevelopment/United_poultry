package com.example.unitedpoultry.AdminHome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class DailyPerformanceStatsViewModel(private val repository: Repository) : ViewModel() {

    fun getDailyPerformanceStats(): LiveData<NetworkStates<Response<BaseResponse<DailyPaymentStatsData>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getDailyPerformanceStats() // ✅ no token
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }

}

