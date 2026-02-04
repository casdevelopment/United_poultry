package com.example.unitedpoultry.status_check

import androidx.lifecycle.LifecycleOwner
import com.example.unitedpoultry.network.Status
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppUtil

object UserStatusChecker {

    fun check(
        lifecycleOwner: LifecycleOwner,
        viewModel: UserStatusViewModel,
        onActive: () -> Unit,
        onInactive: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        viewModel.checkUserStatus()
            .observe(lifecycleOwner) { apiResponse ->

                when (apiResponse.status) {

                    Status.LOADING -> {
                        AppUtil.startLoader(
                            (lifecycleOwner as? android.content.Context) ?: return@observe
                        )
                    }

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()

                        val response = apiResponse.data

                        if (response != null && response.isSuccessful) {

                            val baseResponse = response.body()

                            if (
                                baseResponse != null &&
                                baseResponse.result == "success" &&
                                baseResponse.data?.is_active == true
                            ) {
                                onActive()
                            } else {
                                onInactive()
                            }

                        } else {
                            onError("Status check failed")
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        onError(apiResponse.message ?: "Network error")
                    }
                }
            }
    }
}
