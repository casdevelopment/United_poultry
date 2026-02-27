package com.example.unitedpoultry.NewSale.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.NewSale.model.SaleRequest
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import com.example.unitedpoultry.network.retrofit.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RiderNewSaleViewModel(private val repository: Repository) : ViewModel() {

//    fun createNewSale(request: SaleRequest): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
//        return liveData(Dispatchers.IO) {
//            emit(NetworkStates.loading(null))
//            try {
//                val response = repository.createNewSale(request)
//                emit(NetworkStates.success(response))
//            } catch (e: Exception) {
//                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
//            }
//        }
//    }

    fun createNewSale(
        shopId: RequestBody,
        areaId: RequestBody,
        paymentType: RequestBody,
        collectionAmount: RequestBody,
        borrowedAmount: RequestBody,
        items: RequestBody,
        damageEggs: RequestBody,
    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.createNewSale(
                    shopId,
                    areaId,
                    paymentType,
                    collectionAmount,
                    borrowedAmount,
                    items,
                    damageEggs
                )
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }


    fun createNewSaleCheque(
        shop_id: RequestBody,
        area_id: RequestBody,
        itemsBody: RequestBody,
        damageEggsBody: RequestBody,
        payment_type: RequestBody,
        collection_amount: RequestBody,
        borrowed_amount: RequestBody,
        payment_record: MultipartBody.Part,
        note: RequestBody

    ): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.createNewSaleCheque(
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
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }
}
