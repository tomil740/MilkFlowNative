package com.tomiappdevelopment.milk_flow.core.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.tomiappdevelopment.milk_flow.R
import com.tomiappdevelopment.milk_flow.core.presentation.UiText
import com.tomiappdevelopment.milk_flow.core.workers.util.DemandsAgeSummary
import milkflow.composeapp.generated.resources.Res
import milkflow.composeapp.generated.resources.notif_msg_alert
import milkflow.composeapp.generated.resources.notif_msg_customer
import milkflow.composeapp.generated.resources.notif_msg_regular
import milkflow.composeapp.generated.resources.notif_title_alert
import milkflow.composeapp.generated.resources.notif_title_customer
import milkflow.composeapp.generated.resources.notif_title_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi

class NotificationSender(private val context: Context) {

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
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

    @OptIn(ExperimentalResourceApi::class)
    suspend fun sendDistributorAlertNotification(summary: DemandsAgeSummary) {
        val content = buildString {
            if (summary.over24h.pending > 0) append("• Pending: ${summary.over24h.pending}\n")
            if (summary.over24h.placed > 0) append("• Placed: ${summary.over24h.placed}")
        }.trim()

        val title = UiText.StringResource(Res.string.notif_title_alert)
        val message = UiText.StringResource(Res.string.notif_msg_alert, content)

        val notification = buildBaseNotification(
            title = title.asString2(),
            message = message.asString2(),
            channelId = NotificationChannels.DISTRIBUTOR_ALERT_CHANNEL_ID
        )

        manager.notify(1001, notification)
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun sendDistributorRegularNotification(summary: DemandsAgeSummary) {
        val title = UiText.StringResource(Res.string.notif_title_regular)
        val message = UiText.StringResource(
            Res.string.notif_msg_regular,
            summary.today.pending,
            summary.today.placed
        )

        val notification = buildBaseNotification(
            title = title.asString2(),
            message = message.asString2(),
            channelId = NotificationChannels.DISTRIBUTOR_REGULAR_CHANNEL_ID
        )

        manager.notify(1002, notification)
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun sendCustomerReminderNotification() {
        val title = UiText.StringResource(Res.string.notif_title_customer)
        val message = UiText.StringResource(Res.string.notif_msg_customer)

        val notification = buildBaseNotification(
            title = title.asString2(),
            message = message.asString2(),
            channelId = NotificationChannels.CUSTOMER_REMINDER_CHANNEL_ID
        )

        manager.notify(2001, notification)
    }

    // Common notification builder
    private fun buildBaseNotification(
        title: String,
        message: String,
        channelId: String
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }
}
