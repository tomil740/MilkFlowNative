package com.tomiappdevelopment.milk_flow.core.workers.helperFun

import com.tomiappdevelopment.milk_flow.core.workers.util.DemandsAgeSummary
import com.tomiappdevelopment.milk_flow.core.workers.util.DemandsCount
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


fun mapDemandsByAge(demands: List<Demand>): DemandsAgeSummary {
    val timeZone = TimeZone.currentSystemDefault()
    val nowInstant = Clock.System.now()
    val nowDateTime = nowInstant.toLocalDateTime(timeZone)

    val over24 = demands.filter {
        val createdAtInstant = it.createdAt.toInstant(timeZone)
        val durationMillis = nowInstant.toEpochMilliseconds() - createdAtInstant.toEpochMilliseconds()
        durationMillis > 24 * 60 * 60 * 1000
    }

    val today = demands.filter {
        it.createdAt.date == nowDateTime.date
    }

    return DemandsAgeSummary(
        over24h = DemandsCount(
            pending = over24.count { it.status == Status.pending },
            placed = over24.count { it.status == Status.placed }
        ),
        today = DemandsCount(
            pending = today.count { it.status == Status.pending },
            placed = today.count { it.status == Status.placed }
        )
    )
}

