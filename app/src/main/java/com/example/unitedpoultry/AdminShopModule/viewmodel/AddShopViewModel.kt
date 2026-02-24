package com.example.unitedpoultry.AdminShopModule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddShopViewModel(private val repository: Repository) : ViewModel() {

    fun addShop(
        name: RequestBody? = null,
        contactPerson: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        areaId: RequestBody? = null,
        address: RequestBody? = null,
        discount: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null
    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addShop(name, contactPerson, phoneNumber, areaId, address, discount, isActive, image)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
