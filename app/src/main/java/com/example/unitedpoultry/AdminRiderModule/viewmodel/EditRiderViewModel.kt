package com.example.unitedpoultry.AdminRiderModule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminArea.model.EditAreaRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class EditRiderViewModel(private val repository: Repository) : ViewModel() {

    fun editRider(id: Int, request: RiderEditRequestModel): LiveData<NetworkStates<Response<BaseResponse<RiderModel>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.editRider(id, request)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }



}
