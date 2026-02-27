package com.example.unitedpoultry.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.example.unitedpoultry.AdminArea.viewmodel.AddAreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.AreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.DeleteAreaViewModel
import com.example.unitedpoultry.AdminArea.viewmodel.EditAreaViewModel
import com.example.unitedpoultry.AdminHome.viewmodel.DailyPerformanceStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.ReturnWasteViewModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.AddRiderViewModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.EditRiderViewModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderDailyStatsViewModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderDetailsViewModel
import com.example.unitedpoultry.AdminRiderModule.viewmodel.RiderViewModel
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminLogoutViewModel

import com.example.unitedpoultry.AdminSettingModule.viewmodel.UpdateAdminProfileViewModel
import com.example.unitedpoultry.AdminSettingModule.viewmodel.AdminSettingViewModel

import com.example.unitedpoultry.AdminShopModule.viewmodel.AddShopViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteRiderViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.DeleteShopViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.ShopDetailsViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.ShopListViewModel
import com.example.unitedpoultry.AdminShopModule.viewmodel.UpdateShopViewModel
import com.example.unitedpoultry.Authentications.forgetpassword.ForgetPasswordViewModel
import com.example.unitedpoultry.Authentications.login.viewmodel.LoginViewModel
import com.example.unitedpoultry.Authentications.resetpassword.ResetPasswordViewModel
import com.example.unitedpoultry.Authentications.verifyotp.OtpVerificationViewModel
import com.example.unitedpoultry.History.viewmodel.RiderSaleHistoryViewModel
import com.example.unitedpoultry.History.viewmodel.SaleHistoryDetailViewModel
import com.example.unitedpoultry.NewSale.viewmodel.GetRiderProductViewModel
import com.example.unitedpoultry.NewSale.viewmodel.RiderNewSaleViewModel
import com.example.unitedpoultry.RiderArea.viewmodel.RiderAreaViewModel
import com.example.unitedpoultry.ShopModule.viewmodel.RiderShopListViewModel
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.ShopModule.viewmodel.RiderShopDetailsViewModel
import com.example.unitedpoultry.adminproduct.viewmodel.GetProductViewModel
import com.example.unitedpoultry.exchange_return.viewmodel.RiderReturnOrExchangeSaleViewModel
import com.example.unitedpoultry.network.repo.Repository
import com.example.unitedpoultry.network.retrofit.provideOkHttpClient
import com.example.unitedpoultry.network.retrofit.provideRetrofit
import com.example.unitedpoultry.network.retrofit.provideRetrofitInterface
import com.example.unitedpoultry.rider_expense.ExpenseViewModel
import com.example.unitedpoultry.rider_home.viewmodel.DailyPaymentStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.DailyStatsViewModel
import com.example.unitedpoultry.rider_home.viewmodel.EggPickupViewModel
import com.example.unitedpoultry.status_check.viewmodel.UserStatusViewModel
import com.example.unitedpoultry.util.AppConstants.SHARED_PREF_NAME
import com.example.unitedpoultry.waste_return.viewmodel.RiderReturnProductViewModel
import com.example.unitedpoultry.waste_return.viewmodel.RiderWasteProductViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repoModule = module {
    single {
        Repository(get())
    }
}

val viewModelModule= module{
    viewModel { LoginViewModel(get()) }
    viewModel { ForgetPasswordViewModel(get()) }
    viewModel { OtpVerificationViewModel(get()) }
    viewModel { ResetPasswordViewModel(get()) }

    viewModel { AreaViewModel(get()) }
    viewModel { AddAreaViewModel(get()) }
    viewModel { EditAreaViewModel(get()) }
    viewModel { DeleteAreaViewModel(get()) }


    viewModel { ShopListViewModel(get()) }
    viewModel { AddShopViewModel(get()) }
    viewModel { ShopDetailsViewModel(get()) }
    viewModel { UpdateShopViewModel(get()) }
    viewModel { DeleteShopViewModel(get()) }

    viewModel { AdminLogoutViewModel(get()) }

    viewModel { DailyPerformanceStatsViewModel(get()) }
    viewModel { ReturnWasteViewModel(get()) }

    viewModel { RiderViewModel(get()) }
    viewModel { AddRiderViewModel(get()) }
    viewModel { RiderDetailsViewModel(get()) }
    viewModel { EditRiderViewModel(get()) }
    viewModel { RiderDailyStatsViewModel(get()) }

    viewModel { DeleteRiderViewModel(get()) }
    viewModel { AdminSettingViewModel(get()) }

    viewModel { UpdateAdminProfileViewModel(get()) }

    viewModel { EggPickupViewModel(get()) }
    viewModel { DailyPaymentStatsViewModel(get()) }

    viewModel { GetProductViewModel(get()) }
    viewModel { DailyStatsViewModel(get()) }

    viewModel { RiderAreaViewModel(get()) }
    viewModel { RiderShopListViewModel(get()) }
    viewModel { RiderShopDetailsViewModel(get()) }


    viewModel { GetRiderProductViewModel(get()) }
    viewModel { RiderNewSaleViewModel(get()) }
    viewModel { RiderReturnOrExchangeSaleViewModel(get()) }

    viewModel { RiderSaleHistoryViewModel(get()) }
    viewModel { SaleHistoryDetailViewModel(get()) }

    viewModel { RiderReturnProductViewModel(get()) }
    viewModel { RiderWasteProductViewModel(get()) }

    viewModel { UserStatusViewModel(get()) }

    viewModel { ExpenseViewModel(get()) }

}

val networkModule = module {
    factory { provideOkHttpClient(get()) }
    factory { provideRetrofitInterface(get()) }
    single { provideRetrofit(get()) }
}

val sharedPreferenceModule = module {
    single {
        provideSharedPreference(get())
    }
    single {
        provideEditor(get())
    }
    single {
        SessionManager(get())
    }
}

fun provideSharedPreference(appContext: Context): SharedPreferences {
    return appContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
}

fun provideEditor(sharedPreferences: SharedPreferences): Editor {
    return sharedPreferences.edit()
}
