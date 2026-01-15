package com.example.unitedpoultry.Authentications.verifyotp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class OtpVerificationViewModel(private val repository: Repository) : ViewModel() {

    fun verifyOtp(request: VerifyOtpRequestModel): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response: Response<BaseResponse<Any>> = repository.verifyOtp(request)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }


}
