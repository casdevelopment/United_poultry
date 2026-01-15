package com.example.unitedpoultry.AdminRiderModule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class RiderDetailsViewModel(
    private val repository: Repository) : ViewModel() {

    fun getRiderDetails(
        riderId: Int
    ): LiveData<NetworkStates<Response<BaseResponse<RiderModel>>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))

            try {
                val response = repository.getRiderDetails(riderId)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
