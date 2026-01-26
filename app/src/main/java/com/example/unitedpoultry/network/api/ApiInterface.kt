package com.example.unitedpoultry.network.api

import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.AdminArea.model.EditAreaRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderEditRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderRequestModel
import com.example.unitedpoultry.AdminSettingModule.DataModel.RateData
import com.example.unitedpoultry.AdminSettingModule.DataModel.UpdateRateModel
import com.example.unitedpoultry.AdminShopModule.model.ShopDetailsResponseModel
import com.example.unitedpoultry.AdminShopModule.model.ShopsData
import com.example.unitedpoultry.Authentications.forgetpassword.ForgotPasswordRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginRequestModel
import com.example.unitedpoultry.Authentications.login.model.LoginResponseModel
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordRequestModel
import com.example.unitedpoultry.Authentications.verifyotp.VerifyOtpRequestModel
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("login")  // your login API endpoint
    suspend fun login(@Body loginRequest: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("admin/forgot-password")
    suspend fun forgetPassword(@Body forgetPasswordRequest: ForgotPasswordRequestModel): Response<BaseResponse<Any>>

    @POST("admin/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestModel): Response<BaseResponse<Any>>

    @POST("admin/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestModel): Response<BaseResponse<Any>>

    @GET("admin/areas") // replace with your endpoint
    suspend fun getAreas(@Query("page") page: Int): Response<BaseResponse<AreaDataResponseModel>>

    @POST("admin/areas")  // your login API endpoint
    suspend fun addArea(@Body request: AddAreaRequestModel): Response<BaseResponse<AreaModel>>

    @PUT("admin/areas/{id}")
    suspend fun editArea(@Path("id") id: Int, @Body request: AddAreaRequestModel): Response<BaseResponse<AreaModel>>



    @GET("admin/shops")
    suspend fun getShops(@Query("area_id") areaId: Int, @Query("page") page: Int): Response<BaseResponse<ShopsData>>


    @Multipart
    @POST("admin/shops")
    suspend fun addShop(
        @Part("name") name: RequestBody,
        @Part("contact_person") contactPerson: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("area_id") areaId: RequestBody,
        @Part("address") address: RequestBody,
        @Part("discount_per_petti") discount: RequestBody,
        @Part("is_active") isActive: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<BaseResponse<Any>>


    @GET("admin/shops/{id}")
    suspend fun getShopDetails(
        @Path("id") shopId: Int
    ): Response<BaseResponse<ShopDetailsResponseModel>>


    @Multipart
    @POST("admin/shops/{id}")
    suspend fun updateShop(
        @Path("id") shopId: Int,
        @Part("name") name: RequestBody,
        @Part("contact_person") contactPerson: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part("discount_per_petti") discount: RequestBody,
        @Part("is_active") isActive: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("_method") method: RequestBody
    ): Response<BaseResponse<Any>>



    @DELETE("admin/shops/{id}")
    suspend fun deleteShop(@Path("id") shopId: Int):Response<BaseResponse<Any>>


    @GET("admin/sellers")
    suspend fun getRiders(@Query("page") page: Int): Response<BaseResponse<RiderDataResponceModel>>


    @POST("admin/sellers")  // your login API endpoint
    suspend fun addRider(@Body request: RiderRequestModel): Response<BaseResponse<RiderModel>>


    @GET("admin/sellers/{id}")
    suspend fun getRiderDetails(
        @Path("id") riderId: Int
    ): Response<BaseResponse<RiderModel>>


    @PUT("admin/sellers/{id}")
    suspend fun editRider(@Path("id") id: Int, @Body request: RiderEditRequestModel): Response<BaseResponse<RiderModel>>

    @DELETE("admin/sellers/{id}")
    suspend fun deleteRider(@Path("id") shopId: Int):Response<BaseResponse<Any>>



    @Multipart
    @POST("admin/profile/update")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("username") username: RequestBody,
        @Part("business_name") business_name: RequestBody,
        @Part("address") address: RequestBody,
        @Part image: MultipartBody.Part?

    ): Response<BaseResponse<Any>>

    @POST("admin/products")
    suspend fun createProduct(@Body  fields: HashMap<Any, Any>):Response<BaseResponse<Any>>


    @GET("admin/rates/history")
    suspend fun rateHistory(@Query("page") page: Int):Response<BaseResponse<RateData>>



    @POST("seller/egg-pickup/save-picked")
    suspend fun savePickedEggs(
        @Header("Authorization") token: String,
        @Body request: EggPickupRequest
    ): Response<BaseResponse<EggPickupData>>


    @GET("admin/products")
    suspend fun getProducts(): Response<BaseResponse<ProductData>>

    @GET("seller/egg-pickup/daily-stats")
    suspend fun getDailyStats(): Response<BaseResponse<EggPickupData>>

    @GET("seller/areas") // replace with your endpoint
    suspend fun getRiderAreas(@Query("page") page: Int): Response<BaseResponse<AreaDataResponseModel>>

    @GET("seller/shops")
    suspend fun getRiderShops(@Query("area_id") areaId: Int, @Query("page") page: Int): Response<BaseResponse<ShopsData>>

    @GET("seller/shops/{id}")
    suspend fun getRiderShopDetails(
        @Path("id") shopId: Int
    ): Response<BaseResponse<ShopDetailsResponseModel>>

    @DELETE("admin/areas/{id}")
    suspend fun deleteArea(@Path("id") AreaId: Int):Response<BaseResponse<Any>>

    @GET("admin/rates")
    suspend fun todayRate():Response<BaseResponse<RateData>>

    @POST("admin/rates")
    suspend fun updateRate(@Body updateRateModel:UpdateRateModel):Response<BaseResponse<Any>>

    @POST("admin/logout")
    suspend fun adminLogout():Response<BaseResponse<Any>>


}
