package com.example.unitedpoultry.Authentications.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(loginRequest: LoginRequestModel): LiveData<NetworkStates<Response<BaseResponse<LoginResponseModel>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response: Response<BaseResponse<LoginResponseModel>> = repository.login(loginRequest)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }


}
