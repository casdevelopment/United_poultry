package com.example.unitedpoultry.AdminShopModule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateShopViewModel(private val repository: Repository) : ViewModel() {

    fun updateShop(
        shopId: Int? = null,
        name: RequestBody? = null,
        contactPerson: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        address: RequestBody? = null,
        discount: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null,
        method: RequestBody
    ) = liveData {
        emit(NetworkStates.loading(null))
        try {
            emit(NetworkStates.success(
                repository.updateShop(
                    shopId, name, contactPerson, phoneNumber,
                    address, discount, isActive, image , method
                )
            ))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
