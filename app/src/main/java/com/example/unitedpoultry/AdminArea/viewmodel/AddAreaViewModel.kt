package com.example.unitedpoultry.AdminArea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class AddAreaViewModel(private val repository: Repository) : ViewModel() {

    fun addArea(request: AddAreaRequestModel): LiveData<NetworkStates<Response<BaseResponse<AreaModel>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response: Response<BaseResponse<AreaModel>> = repository.addArea(request)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }


}
