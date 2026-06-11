package com.example.unitedpoultry.NewSale.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.PickedToday
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class GetRiderProductViewModel(private val repository: Repository) : ViewModel() {

    fun getRiderProducts(): LiveData<NetworkStates<Response<BaseResponse<PickedToday>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getRiderProducts()
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }

}

