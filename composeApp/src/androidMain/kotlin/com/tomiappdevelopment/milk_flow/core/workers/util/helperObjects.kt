package com.tomiappdevelopment.milk_flow.core.workers.util

data class DemandsCount(
    val pending: Int,
    val placed: Int
)

data class DemandsAgeSummary(
    val over24h: DemandsCount,
    val today: DemandsCount
)