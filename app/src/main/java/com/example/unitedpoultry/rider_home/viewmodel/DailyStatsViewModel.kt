package com.example.unitedpoultry.rider_home.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class DailyStatsViewModel(private val repository: Repository) : ViewModel() {

    fun getDailyStats(): LiveData<NetworkStates<Response<BaseResponse<EggPickupData>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getDailyStats() // ✅ no token
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }

}

