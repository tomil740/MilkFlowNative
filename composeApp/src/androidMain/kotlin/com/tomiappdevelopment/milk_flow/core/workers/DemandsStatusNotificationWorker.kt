package com.tomiappdevelopment.milk_flow.core.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tomiappdevelopment.milk_flow.core.notifications.NotificationSender
import com.tomiappdevelopment.milk_flow.core.workers.helperFun.mapDemandsByAge
import com.tomiappdevelopment.milk_flow.core.workers.util.isToday
import com.tomiappdevelopment.milk_flow.core.workers.util.toWorkerResult
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncNewDemands
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class DemandsStatusNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val authRepo: AuthRepository by inject()
    private val demandsRepo: DemandsRepository by inject()
    private val syncNewDemands: SyncNewDemands by inject()
    private val notificationSender: NotificationSender by inject()
    override suspend fun doWork(): Result {

        return try {
            val a = authRepo.getAuthState()?.firstOrNull()?.idToken  ?: return Result.success()

            val user: User = authRepo.getUserObjById(a)?: return Result.success()
            val syncResult = syncNewDemands.invoke(uid = user.uid, isDistributor = user.isDistributer)

            when (syncResult) {
                is com.tomiappdevelopment.milk_flow.domain.util.Result.Error<DemandError> -> {
                    return syncResult.error.toWorkerResult()
                }
                is com.tomiappdevelopment.milk_flow.domain.util.Result.Success<Boolean> -> {


                    val pending = demandsRepo.getDemands(Status.pending, uid = user.uid , isDistributor = user.isDistributer).firstOrNull() ?: emptyList()
                    val placed = demandsRepo.getDemands(Status.placed, uid = user.uid , isDistributor = user.isDistributer).firstOrNull() ?: emptyList()

                    val allDemands = placed+pending

                    if (user.isDistributer) {
                        val summary = mapDemandsByAge(allDemands)
                        if (summary.over24h.pending > 0 || summary.over24h.placed > 0) {
                            notificationSender.sendDistributorAlertNotification(summary)
                        }
                        if (summary.today.placed > 0 || summary.today.pending > 0){
                            notificationSender.sendDistributorRegularNotification(summary)

                        }
                    } else {
                        if (allDemands.none { it.createdAt.isToday() }) {
                            notificationSender.sendCustomerReminderNotification()
                        }
                    }
                }
            }


            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
    }
}