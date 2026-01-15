package com.example.unitedpoultry.Authentications.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class ResetPasswordViewModel(private val repository: Repository) : ViewModel() {

    fun resetPassword(request: ResetPasswordRequestModel): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response: Response<BaseResponse<Any>> = repository.resetPassword(request)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }


}
