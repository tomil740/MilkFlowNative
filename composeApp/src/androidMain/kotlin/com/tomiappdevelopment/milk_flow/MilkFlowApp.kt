package com.tomiappdevelopment.milk_flow

import android.app.Application
import android.os.Build
import com.tomiappdevelopment.milk_flow.core.notifications.createNotificationChannels
import com.tomiappdevelopment.milk_flow.core.workers.scheduleDemandNotifications
import com.tomiappdevelopment.milk_flow.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class MilkFlowApp: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels(this)
        }
        scheduleDemandNotifications(this)

        initKoin {
            androidLogger()
            androidContext(this@MilkFlowApp)
        }
    }

}