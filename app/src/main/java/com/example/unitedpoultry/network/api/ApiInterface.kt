package com.example.unitedpoultry.network.api

import com.example.unitedpoultry.AdminArea.model.AddAreaRequestModel
import com.example.unitedpoultry.AdminArea.model.AreaDataResponseModel
import com.example.unitedpoultry.AdminArea.model.AreaModel
import com.example.unitedpoultry.rider_home.model.ReturnWasteRequestModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderDataResponceModel
import com.example.unitedpoultry.AdminRiderModule.model.RiderModel
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
import com.example.unitedpoultry.Collection.model.CollectionResponse
import com.example.unitedpoultry.History.model.CollectionHistory
import com.example.unitedpoultry.History.model.ExpenseItem
import com.example.unitedpoultry.History.model.HistoryData
import com.example.unitedpoultry.History.model.SaleHistory
import com.example.unitedpoultry.NewSale.model.SaleResponse
import com.example.unitedpoultry.Profile.model.ChangePasswordRequestModel
import com.example.unitedpoultry.adminproduct.model.ProductData
import com.example.unitedpoultry.exchange_return.model.ReturnOrExchangeRequest
import com.example.unitedpoultry.network.retrofit.BaseResponse
import com.example.unitedpoultry.rider_expense.Model.ExpenseHeadData
import com.example.unitedpoultry.rider_expense.Model.ExpenseModel
import com.example.unitedpoultry.rider_home.model.DailyPaymentStatsData
import com.example.unitedpoultry.rider_home.model.EggPickupData
import com.example.unitedpoultry.rider_home.model.EggPickupRequest
import com.example.unitedpoultry.rider_home.model.PickedToday
import com.example.unitedpoultry.status_check.model.UserStatusResponse
import com.example.unitedpoultry.waste_return.model.RiderReturnRequest
import com.example.unitedpoultry.waste_return.model.RiderWasteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("admin/forgot-password")
    suspend fun forgetPassword(@Body forgetPasswordRequest: ForgotPasswordRequestModel): Response<BaseResponse<Any>>

    @POST("admin/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestModel): Response<BaseResponse<Any>>

    @POST("admin/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestModel): Response<BaseResponse<Any>>

    @GET("admin/areas")
    suspend fun getAreas(@Query("page") page: Int): Response<BaseResponse<AreaDataResponseModel>>

    @POST("admin/areas")
    suspend fun addArea(@Body request: AddAreaRequestModel): Response<BaseResponse<AreaModel>>

    @PUT("admin/areas/{id}")
    suspend fun editArea(@Path("id") id: Int, @Body request: AddAreaRequestModel): Response<BaseResponse<AreaModel>>



    @GET("admin/shops")
    suspend fun getShops(@Query("area_id") areaId: Int, @Query("page") page: Int): Response<BaseResponse<ShopsData>>


    @Multipart
    @POST("admin/shops")
    suspend fun addShop(
        @Part("name") name: RequestBody? = null,
        @Part("contact_person") contactPerson: RequestBody? = null,
        @Part("phone_number") phoneNumber: RequestBody? = null,
        @Part("area_id") areaId: RequestBody? = null,
        @Part("address") address: RequestBody? = null,
        @Part("discount_per_petti") discount: RequestBody? = null,
        @Part("is_active") isActive: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Response<BaseResponse<Any>>


    @GET("admin/shops/{id}")
    suspend fun getShopDetails(
        @Path("id") shopId: Int
    ): Response<BaseResponse<ShopDetailsResponseModel>>


    @Multipart
    @POST("admin/shops/{id}")
    suspend fun updateShop(
        @Path("id") shopId: Int? = null,
        @Part("name") name: RequestBody? = null,
        @Part("contact_person") contactPerson: RequestBody? = null,
        @Part("phone_number") phoneNumber: RequestBody? = null,
        @Part("address") address: RequestBody? = null,
        @Part("discount_per_petti") discount: RequestBody? = null,
        @Part("is_active") isActive: RequestBody? = null,
        @Part image: MultipartBody.Part? = null,
        @Part("_method") method: RequestBody
    ): Response<BaseResponse<Any>>



    @DELETE("admin/shops/{id}")
    suspend fun deleteShop(@Path("id") shopId: Int):Response<BaseResponse<Any>>


    @GET("admin/sellers")
    suspend fun getRiders(@Query("page") page: Int): Response<BaseResponse<RiderDataResponceModel>>


//    @POST("admin/sellers")  // your login API endpoint
//    suspend fun addRider(@Body request: RiderRequestModel): Response<BaseResponse<RiderModel>>

    @Multipart
    @POST("admin/sellers")
    suspend fun addRider(
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("username") username: RequestBody? = null,
        @Part("phone_number") phoneNumber: RequestBody? = null,
        @Part("cnic") cnic: RequestBody? = null,
        @Part("address") address: RequestBody? = null,
        @Part("password") password: RequestBody? = null,
        @Part("is_active") isActive: RequestBody?,
        @Part image: MultipartBody.Part? = null // optional file upload
    ): Response<BaseResponse<Any>>

    @GET("admin/sellers/{id}")
    suspend fun getRiderDetails(
        @Path("id") riderId: Int
    ): Response<BaseResponse<RiderModel>>


    @Multipart
    @POST("admin/sellers/{id}")  // update endpoint
    suspend fun editRider(
        @Path("id") riderId: Int,
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("username") username: RequestBody? = null,
        @Part("phone_number") phoneNumber: RequestBody? = null,
        @Part("cnic") cnic: RequestBody? = null,
        @Part("address") address: RequestBody? = null,
      //  @Part("password") password: RequestBody? = null,
        @Part("is_active") isActive: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Response<BaseResponse<RiderModel>>


    @DELETE("admin/sellers/{id}")
    suspend fun deleteRider(@Path("id") shopId: Int):Response<BaseResponse<Any>>



    @Multipart
    @POST("admin/profile/update")
    suspend fun updateProfile(
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("phone") phone: RequestBody? = null,
        @Part("username") username: RequestBody? = null,
        @Part("business_name") business_name: RequestBody? = null,
        @Part("address") address: RequestBody? = null,
        @Part image: MultipartBody.Part? = null

    ): Response<BaseResponse<LoginResponseModel>>

    @POST("admin/products")
    suspend fun createProduct(@Body  fields: HashMap<Any, Any>):Response<BaseResponse<Any>>


    @GET("admin/rates/history")
    suspend fun rateHistory(@Query("page") page: Int):Response<BaseResponse<RateData>>



    @POST("seller/egg-pickup/save-picked")
    suspend fun savePickedEggs(
        @Body request: EggPickupRequest
    ): Response<BaseResponse<Any>>


    @GET("admin/products")
    suspend fun getProducts(): Response<BaseResponse<ProductData>>

    @GET("seller/egg-pickup/daily-stats")
    suspend fun getDailyStats(): Response<BaseResponse<EggPickupData>>

    @GET("seller/areas") // replace with your endpoint
    suspend fun getRiderAreas(@Query("page") page: Int): Response<BaseResponse<AreaDataResponseModel>>

    @GET("seller/shops")
    suspend fun getRiderShops(@Query("area_id") areaId: Int, @Query("page") page: Int,@Query("status") currentFilter: String): Response<BaseResponse<ShopsData>>


    @GET("seller/shops/{id}")
    suspend fun getRiderShopDetails(
        @Path("id") shopId: Int
    ): Response<BaseResponse<ShopDetailsResponse>>


    @DELETE("admin/areas/{id}")
    suspend fun deleteArea(@Path("id") AreaId: Int):Response<BaseResponse<Any>>

    @GET("admin/rates")
    suspend fun todayRate():Response<BaseResponse<RateData>>

    @POST("admin/rates")
    suspend fun updateRate(@Body updateRateModel:UpdateRateModel):Response<BaseResponse<Any>>

    @POST("admin/logout")
    suspend fun adminLogout():Response<BaseResponse<Any>>

    @GET("seller/egg-pickup/picked-today")
    suspend fun getRiderProducts(): Response<BaseResponse<PickedToday>>

//    @POST("seller/sales") // Replace with your actual endpoint
//    suspend fun createNewSale(@Body request: SaleRequest): Response<BaseResponse<Any>>

    @Multipart
    @POST("seller/sales")
    suspend fun createNewSale(
        @Part("shop_id") shopId: RequestBody,
        @Part("area_id") areaId: RequestBody,
        @Part("payment_type") paymentType: RequestBody,
        @Part("collection_amount") collectionAmount: RequestBody,
        @Part("borrowed_amount") borrowedAmount: RequestBody,
        //@Part("previous_payment") previous_payment: RequestBody,
        @Part("items") items: RequestBody
      //  @Part("damage_eggs") damageEggs: RequestBody
    ): Response<BaseResponse<SaleResponse>>


    @Multipart
    @POST("seller/sales")
    suspend fun createNewSaleCheque(
        @Part("shop_id") shop_id: RequestBody,
        @Part("area_id") area_id: RequestBody,
        @Part("items") itemsBody: RequestBody,
       // @Part("damage_eggs") damageEggsBody: RequestBody,
        @Part("payment_type") payment_type: RequestBody,
        @Part("collection_amount") collection_amount: RequestBody,
        @Part("borrowed_amount") borrowed_amount: RequestBody,
        @Part payment_record: MultipartBody.Part,
        @Part("payment_note") note: RequestBody,
        ): Response<BaseResponse<SaleResponse>>


    @POST("seller/sale-returns") // Replace with your actual endpoint
    suspend fun ReturnOrExchangeSale(@Body request: ReturnOrExchangeRequest): Response<BaseResponse<Any>>


    @GET("seller/dashboard/stats")
    suspend fun getDailyPaymentStats(): Response<BaseResponse<DailyPaymentStatsData>>

    @GET("admin/dashboard/stats")
    suspend fun getDailyPerformanceStats(): Response<BaseResponse<DailyPaymentStatsData>>


    @POST("seller/egg-pickup/save-record")  // your login API endpoint
    suspend fun submitReturnWaste(@Body request: ReturnWasteRequestModel): Response<BaseResponse<Any>>

//    @GET("seller/sales")
//    suspend fun getSaleHistory(
//        @Query("page") page: Int,
//        @Query("duration") duration: String
//    ): Response<BaseResponse<SaleHistoryData>>
//


    @GET("admin/egg-pickup/daily-stats")
    suspend fun getRiderDailyStats(
        @Query("seller_id") sellerId: Int
    ): Response<BaseResponse<EggPickupData>>



    @POST("seller/egg-pickup/save-returned") // Replace with your actual endpoint
    suspend fun RiderReturnRequest(@Body request: RiderReturnRequest): Response<BaseResponse<Any>>


    @POST("seller/egg-pickup/save-waste")
    suspend fun RiderWasteProduct(@Body request: RiderWasteRequest): Response<BaseResponse<Any>>

    @GET("seller/account/status")
    suspend fun checkUserStatus(): Response<BaseResponse<UserStatusResponse>>



    @Multipart
    @POST("seller/expenses")
    suspend fun addExpenseByCash(
        @Part("expenses") expenses: RequestBody,
        @Part("total_amount") totalAmount: RequestBody,
        @Part("payment_type") paymentType: RequestBody,
        @Part("expense_date") expenseDate: RequestBody,
        @Part("note") note: RequestBody? = null
    ): Response<BaseResponse<ExpenseModel>>

    @Multipart
    @POST("seller/expenses")
    suspend fun addExpenseByCheque(
        @Part("expenses") expenses: RequestBody,
        @Part("total_amount") totalAmount: RequestBody,
        @Part("payment_type") payment_type: RequestBody,
        @Part("expense_date") expense_date: RequestBody,
        @Part("note") note: RequestBody? = null,
        @Part image: MultipartBody.Part,
        @Part("payment_note") payment_note: RequestBody? = null,
    ): Response<BaseResponse<ExpenseModel>>

    @Multipart
    @POST("seller/collections")
    suspend fun addCollectionByCash(
        @Part("shop_id") shop_id: RequestBody,
        @Part("payment_type") payment_type: RequestBody,
        @Part("amount") amount: RequestBody,
    ): Response<BaseResponse<CollectionResponse>>



    @Multipart
    @POST("seller/collections")
    suspend fun addCollectionByCheque(
        @Part("shop_id") shop_id: RequestBody,
        @Part("payment_type") payment_type: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("payment_note") payment_note: RequestBody? = null,

    ): Response<BaseResponse<CollectionResponse>>



        @GET("seller/history")
        suspend fun getHistory(
            @Query("page") page: Int,
            @Query("duration") duration: String,
            @Query("type") type: String
        ): Response<BaseResponse<HistoryData>>

    @GET("seller/sales/{id}")
    suspend fun getSaleDetail(@Path("id") id: Int): Response<BaseResponse<SaleHistory>>


    @GET("seller/collections/{id}")
    suspend fun getCollectionDetail(@Path("id") id: Int): Response<BaseResponse<CollectionHistory>>


    @POST("seller/change-password")  // your login API endpoint
    suspend fun changePassword(@Body request: ChangePasswordRequestModel): Response<BaseResponse<Any>>


    @GET("seller/expenses/{id}")
    suspend fun getExpenseDetail(@Path("id") id: Int): Response<BaseResponse<ExpenseItem>>


    @GET("seller/expenses/heads")
    suspend fun getExpenses(): Response<BaseResponse<ExpenseHeadData>>

}
