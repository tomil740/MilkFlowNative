package com.tomiappdevelopment.milk_flow

import android.app.Application
import com.tomiappdevelopment.milk_flow.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class MilkFlowApp: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MilkFlowApp)
        }
    }
}