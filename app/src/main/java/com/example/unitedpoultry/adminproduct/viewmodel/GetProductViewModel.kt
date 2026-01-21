package com.example.unitedpoultry.adminproduct.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class GetProductViewModel(private val repository: Repository) : ViewModel() {

    fun getProducts(): LiveData<NetworkStates<Response<BaseResponse<ProductData>>>> =
        liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getProducts() // ✅ no token
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }

}

