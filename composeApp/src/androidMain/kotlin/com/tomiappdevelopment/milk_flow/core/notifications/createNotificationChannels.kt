package com.tomiappdevelopment.milk_flow.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannels(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val alertChannel = NotificationChannel(
        NotificationChannels.DISTRIBUTOR_ALERT_CHANNEL_ID,
        "Distributor Alerts",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Urgent alerts for overdue demands"
    }

    val regularChannel = NotificationChannel(
        NotificationChannels.DISTRIBUTOR_REGULAR_CHANNEL_ID,
        "Distributor Daily",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Daily notification for distributor"
    }

    val customerChannel = NotificationChannel(
        NotificationChannels.CUSTOMER_REMINDER_CHANNEL_ID,
        "Customer Reminder",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Daily reminder for customer to make a demand"
    }

    manager.createNotificationChannel(alertChannel)
    manager.createNotificationChannel(regularChannel)
    manager.createNotificationChannel(customerChannel)
}
