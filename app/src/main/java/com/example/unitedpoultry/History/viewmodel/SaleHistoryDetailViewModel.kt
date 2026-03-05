package com.example.unitedpoultry.History.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.unitedpoultry.History.model.CollectionHistory
import com.example.unitedpoultry.History.model.ExpenseItem
import com.example.unitedpoultry.History.model.SaleHistory
import com.example.unitedpoultry.History.model.SaleHistoryData
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.network.NetworkStates
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class SaleHistoryDetailViewModel (private val repository: Repository) : ViewModel() {

    fun getSaleDetail( id: Int): LiveData<NetworkStates<Response<BaseResponse<SaleHistory>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getSaleDetail(id)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }


    fun getCollectionDetail( id: Int): LiveData<NetworkStates<Response<BaseResponse<CollectionHistory>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getCollectionDetail(id)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }

    fun getExpenseDetail( id: Int): LiveData<NetworkStates<Response<BaseResponse<ExpenseItem>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getExpenseDetail(id)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
}