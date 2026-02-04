package com.example.unitedpoultry.AdminRiderModule.viewmodel

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

class AddRiderViewModel(private val repository: Repository) : ViewModel() {

    fun addRider(
        name: RequestBody,
        email: RequestBody,
        username: RequestBody,
        phoneNumber: RequestBody,
        cnic: RequestBody,
        address: RequestBody,
        password: RequestBody,
        isActive: RequestBody,
        image: MultipartBody.Part
    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addRider(
                name,
                email,
                username,
                phoneNumber,
                cnic,
                address,
                password,
                isActive,
                image
            )
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }





}
