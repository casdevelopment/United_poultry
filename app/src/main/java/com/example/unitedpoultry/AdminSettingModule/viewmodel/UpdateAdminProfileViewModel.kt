package com.example.unitedpoultry.AdminSettingModule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateAdminProfileViewModel(private val repository: Repository) : ViewModel() {

    fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody,
        username: RequestBody,
        business_name: RequestBody,
        address: RequestBody,
        image: MultipartBody.Part?,
        method: RequestBody
    ) = liveData {
        emit(NetworkStates.loading(null))
        try {
            emit(NetworkStates.success(
                repository.updateProfile(
                     name, email, phone,username,business_name,address,image, method
                )
            ))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
