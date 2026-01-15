package com.example.unitedpoultry

import android.app.Application
import com.example.unitedpoultry.modules.networkModule
import com.example.unitedpoultry.modules.repoModule
import com.example.unitedpoultry.modules.sharedPreferenceModule
import com.example.unitedpoultry.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(
                networkModule,
                repoModule,
                viewModelModule,
                sharedPreferenceModule
            ))
        }
    }
}
