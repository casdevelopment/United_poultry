package com.example.unitedpoultry.status_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.status_check.model.UserStatusResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class UserStatusViewModel(private val repository: Repository) : ViewModel() {

    fun checkUserStatus(): LiveData<NetworkStates<Response<BaseResponse<UserStatusResponse>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.checkUserStatus()
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(
                    NetworkStates.error(
                        data = null,
                        message = e.message ?: "Something went wrong"
                    )
                )
            }
        }
    }
}
