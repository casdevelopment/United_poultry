package com.example.unitedpoultry.AdminShopModule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class ShopDetailsViewModel(
    private val repository: Repository) : ViewModel() {

    fun getShopDetails(
        shopId: Int
    ): LiveData<NetworkStates<Response<BaseResponse<ShopDetailsResponseModel>>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))

            try {
                val response = repository.getShopDetails(shopId)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
