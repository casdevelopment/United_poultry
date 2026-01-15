package com.example.unitedpoultry.AdminRiderModule.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class RiderViewModel(private val repository: Repository) : ViewModel() {

    fun getRiders(page: Int): LiveData<NetworkStates<Response<BaseResponse<RiderDataResponceModel>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getRiders(page) // repository should accept page param
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }

}

