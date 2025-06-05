package com.tomiappdevelopment.milk_flow.data.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


fun LocalDateTime.toLong(): Long{
    return this.toInstant(TimeZone.UTC).toEpochMilliseconds()

}

fun Long.toLocalDateTime(): LocalDateTime {
    // Convert the timestamp (in milliseconds) to an Instant
    val instant = Instant.fromEpochMilliseconds(this)
    // Convert Instant to LocalDateTime in UTC (or another timezone)
    return instant.toLocalDateTime(TimeZone.UTC)
}
