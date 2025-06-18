package com.tomiappdevelopment.milk_flow.core.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleDemandNotifications(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val morningDelay = calculateInitialDelay(hour = 8)
    val eveningDelay = calculateInitialDelay(hour = 16)

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val morningWork = PeriodicWorkRequestBuilder<DemandsStatusNotificationWorker>(
        24, TimeUnit.HOURS
    )
        .setInitialDelay(morningDelay, TimeUnit.MILLISECONDS)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            10, TimeUnit.MINUTES
        )
        .setConstraints(constraints)
        .addTag("demands_worker_morning")
        .build()

    val eveningWork = PeriodicWorkRequestBuilder<DemandsStatusNotificationWorker>(
        24, TimeUnit.HOURS
    )
        .setInitialDelay(eveningDelay, TimeUnit.MILLISECONDS)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            10, TimeUnit.MINUTES
        )
        .setConstraints(constraints)
        .addTag("demands_worker_evening")
        .build()

    workManager.enqueueUniquePeriodicWork(
        "demands_worker_morning",
        ExistingPeriodicWorkPolicy.UPDATE,
        morningWork
    )

    workManager.enqueueUniquePeriodicWork(
        "demands_worker_evening",
        ExistingPeriodicWorkPolicy.UPDATE,
        eveningWork
    )
}


fun calculateInitialDelay(hour: Int): Long {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If the target time already passed today, schedule for tomorrow
    if (target.timeInMillis <= now.timeInMillis) {
        target.add(Calendar.DAY_OF_YEAR, 1)
    }

    return target.timeInMillis - now.timeInMillis
}