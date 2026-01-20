package com.example.unitedpoultry.network.repo


import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminArea.model.EditAreaRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderRequestModel
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.AdminShopModule.model.ShopsData
import com.example.unitedpoultry.Authentications.forgetpassword.ForgotPasswordRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordRequestModel
import com.example.unitedpoultry.Authentications.verifyotp.VerifyOtpRequestModel
import com.example.unitedpoultry.network.api.ApiInterface
import com.example.unitedpoultry.network.retrofit.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class Repository(private val api: ApiInterface) {

    suspend fun login(loginRequest: LoginRequestModel): Response<BaseResponse<LoginResponseModel>> {
        return api.login(loginRequest)
    }

    suspend fun forgetPassword(forgetPasswordRequest: ForgotPasswordRequestModel): Response<BaseResponse<Any>> {
        return api.forgetPassword(forgetPasswordRequest)
    }

    suspend fun verifyOtp(request: VerifyOtpRequestModel): Response<BaseResponse<Any>> {
        return api.verifyOtp(request)
    }

    suspend fun resetPassword(request: ResetPasswordRequestModel): Response<BaseResponse<Any>> {
        return api.resetPassword(request)
    }

    suspend fun getAreas(page: Int = 1): Response<BaseResponse<AreaDataResponseModel>> {
        return api.getAreas(page = page)
    }

    suspend fun addArea(request: AddAreaRequestModel): Response<BaseResponse<AreaModel>> {
        return api.addArea(request)
    }

    suspend fun editArea(id: Int, request: EditAreaRequestModel): Response<BaseResponse<AreaModel>> {
        return api.editArea(id, request)
    }

    suspend fun getShops(areaId: Int,page: Int = 1): Response<BaseResponse<ShopsData>> {
        return api.getShops(areaId,page = page)
    }

    suspend fun addShop(
        name: RequestBody,
        contactPerson: RequestBody,
        phoneNumber: RequestBody,
        areaId: RequestBody,
        address: RequestBody,
        discount: RequestBody,
        isActive: RequestBody,
        image: MultipartBody.Part
    ): Response<BaseResponse<Any>> {
        return api.addShop(name, contactPerson, phoneNumber, areaId, address, discount, isActive, image)
    }


    suspend fun getShopDetails(
        shopId: Int
    ): Response<BaseResponse<ShopDetailsResponseModel>> {
        return api.getShopDetails(shopId)
    }


    suspend fun updateShop(
        shopId: Int,
        name: RequestBody,
        contactPerson: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        discount: RequestBody,
        isActive: RequestBody,
        image: MultipartBody.Part?,
        method: RequestBody
    ): Response<BaseResponse<Any>> {
        return api.updateShop(
            shopId, name, contactPerson, phoneNumber,
            address, discount, isActive, image ,method
        )
    }


    suspend fun deleteShop(ShopId: Int): Response<BaseResponse<Any>> {
        return api.deleteShop(ShopId)
    }

    suspend fun getRiders(page: Int = 1): Response<BaseResponse<RiderDataResponceModel>> {
        return api.getRiders(page = page)
    }

    suspend fun addRider(request: RiderRequestModel): Response<BaseResponse<RiderModel>> {
        return api.addRider(request)
    }


    suspend fun getRiderDetails(
        riderId: Int
    ): Response<BaseResponse<RiderModel>> {
        return api.getRiderDetails(riderId)
    }

    suspend fun editRider(id: Int, request: RiderEditRequestModel): Response<BaseResponse<RiderModel>> {
        return api.editRider(id, request)
    }

    suspend fun deleteRider(ShopId: Int): Response<BaseResponse<Any>> {
        return api.deleteRider(ShopId)
    }

    suspend fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody,
        username: RequestBody,
        business_name: RequestBody,
        address: RequestBody,
        image: MultipartBody.Part?,
        method: RequestBody
    ): Response<BaseResponse<Any>> {
        return api.updateProfile(
            name, email, phone,username,business_name,address,image, method
        )
    }

}

