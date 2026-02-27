package com.example.unitedpoultry.network.repo


import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.rider_home.model.ReturnWasteRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderRequestModel
import com.example.unitedpoultry.AdminSettingModule.DataModel.RateData
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponse
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.AdminShopModule.model.ShopsData
import com.example.unitedpoultry.Authentications.forgetpassword.ForgotPasswordRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordRequestModel
import com.example.unitedpoultry.Authentications.verifyotp.VerifyOtpRequestModel
import com.example.unitedpoultry.History.model.SaleHistoryData
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.NewSale.model.PickedItemsResponse
import com.example.unitedpoultry.NewSale.model.SaleRequest
import com.example.unitedpoultry.exchange_return.model.ReturnOrExchangeRequest
import com.example.unitedpoultry.network.api.ApiInterface
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import com.example.unitedpoultry.status_check.model.UserStatusResponse
import com.example.unitedpoultry.waste_return.model.RiderReturnRequest
import com.example.unitedpoultry.waste_return.model.RiderWasteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    suspend fun editArea(id: Int, request: AddAreaRequestModel): Response<BaseResponse<AreaModel>> {
        return api.editArea(id, request)
    }

    suspend fun getShops(areaId: Int,page: Int = 1): Response<BaseResponse<ShopsData>> {
        return api.getShops(areaId,page = page)
    }

    suspend fun addShop(
        name: RequestBody? = null,
        contactPerson: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        areaId: RequestBody? = null,
        address: RequestBody? = null,
        discount: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null
    ): Response<BaseResponse<Any>> {
        return api.addShop(name, contactPerson, phoneNumber, areaId, address, discount, isActive, image)
    }


    suspend fun getShopDetails(
        shopId: Int
    ): Response<BaseResponse<ShopDetailsResponseModel>> {
        return api.getShopDetails(shopId)
    }


    suspend fun updateShop(
        shopId: Int? = null,
        name: RequestBody? = null,
        contactPerson: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        address: RequestBody? = null,
        discount: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null,
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

//    suspend fun addRider(request: RiderRequestModel): Response<BaseResponse<RiderModel>> {
//        return api.addRider(request)
//    }

    suspend fun addRider(
        name: RequestBody? = null,
        email: RequestBody? = null,
//        username: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        cnic: RequestBody? = null,
        address: RequestBody? = null,
        password: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null
    ): Response<BaseResponse<Any>> {
        return api.addRider(name, email,  phoneNumber, cnic, address, password,isActive, image)
    }



    suspend fun getRiderDetails(
        riderId: Int
    ): Response<BaseResponse<RiderModel>> {
        return api.getRiderDetails(riderId)
    }

//    suspend fun editRider(id: Int, request: RiderEditRequestModel): Response<BaseResponse<RiderModel>> {
//        return api.editRider(id, request)
//    }


    suspend fun editRider(
        id: Int,
        name: RequestBody? = null,
        email: RequestBody? = null,
//        username: RequestBody? = null,
        phoneNumber: RequestBody? = null,
        cnic: RequestBody? = null,
        address: RequestBody? = null,
        password: RequestBody? = null,
        isActive: RequestBody? = null,
        image: MultipartBody.Part? = null
    ): Response<BaseResponse<RiderModel>> {
        return api.editRider(id,name, email, phoneNumber, cnic, address, password,isActive, image)
    }

    suspend fun deleteRider(ShopId: Int): Response<BaseResponse<Any>> {
        return api.deleteRider(ShopId)
    }

    suspend fun updateProfile(
        name: RequestBody? = null,
        email: RequestBody? = null,
        phone: RequestBody? = null,
        username: RequestBody? = null,
        business_name: RequestBody? = null,
        address: RequestBody? = null,
        image: MultipartBody.Part? = null,

    ): Response<BaseResponse<LoginResponseModel>> {
        return api.updateProfile(
            name, email, phone, username, business_name, address, image
        )
    }

    suspend fun createProduct(fields: HashMap<Any, Any>): Response<BaseResponse<Any>> {
        return api.createProduct(fields)
    }

    suspend fun rateHistory(page: Int): Response<BaseResponse<RateData>> {
        return api.rateHistory(page)

    }
    suspend fun todayRate(): Response<BaseResponse<RateData>> {
        return api.todayRate()
    }
    suspend fun updateRate(updateRateModel:UpdateRateModel): Response<BaseResponse<Any>> {
        return api.updateRate(updateRateModel)
    }

    suspend fun savePickedEggs(request: EggPickupRequest): Response<BaseResponse<Any>> {
        return api.savePickedEggs(request)
    }

    suspend fun getProducts() = api.getProducts()

    suspend fun getDailyStats() = api.getDailyStats()

    suspend fun getRiderAreas(page: Int = 1): Response<BaseResponse<AreaDataResponseModel>> {
        return api.getRiderAreas(page = page)
    }

    suspend fun getRiderShops(areaId: Int,page: Int = 1,currentFilter: String): Response<BaseResponse<ShopsData>> {
        return api.getRiderShops(areaId,page = page,currentFilter)
    }

    suspend fun getRiderShopDetails(
        shopId: Int
    ): Response<BaseResponse<ShopDetailsResponse>> {
        return api.getRiderShopDetails(shopId)
    }

    suspend fun deleteArea(AreaId: Int): Response<BaseResponse<Any>> {
        return api.deleteArea(AreaId)
    }

    suspend fun adminLogout(): Response<BaseResponse<Any>> {
        return api.adminLogout()
    }

    suspend fun getRiderProducts(): Response<BaseResponse<PickedItemsResponse>> {
        return api.getRiderProducts()
    }

//    suspend fun createNewSale(request: SaleRequest): Response<BaseResponse<Any>> {
//        return api.createNewSale(request)
//    }

    suspend fun createNewSale(
        shopId: RequestBody,
        areaId: RequestBody,
        paymentType: RequestBody,
        collectionAmount: RequestBody,
        borrowedAmount: RequestBody,
        items: RequestBody,
        damageEggs: RequestBody
    ): Response<BaseResponse<Any>> {
        return api.createNewSale(shopId, areaId, paymentType, collectionAmount, borrowedAmount, items, damageEggs)
    }


    suspend fun createNewSaleCheque(
        shop_id: RequestBody,
        area_id: RequestBody,
        itemsBody: RequestBody,
        damageEggsBody: RequestBody,
        payment_type: RequestBody,
        collection_amount: RequestBody,
        borrowed_amount: RequestBody,
        payment_record: MultipartBody.Part,
        note: RequestBody
    ): Response<BaseResponse<Any>> {
        return api.createNewSaleCheque(
            shop_id,
            area_id,
            itemsBody,
            damageEggsBody,
            payment_type,
            collection_amount,
            borrowed_amount,
            payment_record,
            note
        )
    }

    suspend fun ReturnOrExchangeSale(request: ReturnOrExchangeRequest): Response<BaseResponse<Any>> {
        return api.ReturnOrExchangeSale(request)
    }

    suspend fun getDailyPaymentStats() = api.getDailyPaymentStats()

    suspend fun getDailyPerformanceStats() = api.getDailyPerformanceStats()

    suspend fun submitReturnWaste(request: ReturnWasteRequestModel): Response<BaseResponse<Any>> {
        return api.submitReturnWaste(request)
    }


    suspend fun getSaleHistory(page: Int, duration: String): Response<BaseResponse<SaleHistoryData>> {
        return api.getSaleHistory(page, duration)
    }

    suspend fun getSaleDetail(id: Int): Response<BaseResponse<SaleItem>> {
        return api.getSaleDetail(id)
    }

    suspend fun getRiderDailyStats(id: Int) = api.getRiderDailyStats(id)


    suspend fun RiderReturnRequest(request: RiderReturnRequest): Response<BaseResponse<Any>> {
        return api.RiderReturnRequest(request)
    }

    suspend fun RiderWasteProduct(request: RiderWasteRequest): Response<BaseResponse<Any>> {
        return api.RiderWasteProduct(request)
    }

    suspend fun checkUserStatus(): Response<BaseResponse<UserStatusResponse>> {
        return api.checkUserStatus()
    }



}

