package com.example.unitedpoultry.AdminSettingModule.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminSettingModule.DataModel.RateData
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class AdminSettingViewModel (private val repository: Repository) : ViewModel() {


    fun getRates(page: Int): LiveData<NetworkStates<Response<BaseResponse<RiderDataResponceModel>>>> {
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

    fun createProduct(fields: HashMap<Any, Any>): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.createProduct(fields) // repository should accept page param
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                Log.e("createProduct", "createProduct: ${e.message}")
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
    fun rateHistory(page: Int): LiveData<NetworkStates<Response<BaseResponse<RateData>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.rateHistory(page) // repository should accept page param
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                Log.e("createProduct", "createProduct: ${e.message}")
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
    fun todayRate(): LiveData<NetworkStates<Response<BaseResponse<RateData>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.todayRate() // repository should accept page param
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                Log.e("createProduct", "createProduct: ${e.message}")
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
    fun updateRate(updateRateModel:UpdateRateModel): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.updateRate(updateRateModel) // repository should accept page param
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                Log.e("createProduct", "createProduct: ${e.message}")
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }

}