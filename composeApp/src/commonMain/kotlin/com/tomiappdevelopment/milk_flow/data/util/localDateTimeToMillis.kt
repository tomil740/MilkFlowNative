package com.tomiappdevelopment.milk_flow.data.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import network.chaintech.utils.now


fun LocalDateTime.toLong(): Long{
    return this.toInstant(TimeZone.UTC).toEpochMilliseconds()

}

fun Long.toLocalDateTime(): LocalDateTime {
    // Convert the timestamp (in milliseconds) to an Instant
    val instant = Instant.fromEpochMilliseconds(this)
    // Convert Instant to LocalDateTime in UTC (or another timezone)
    return instant.toLocalDateTime(TimeZone.UTC)
}

fun LocalDateTime.toISO(): String {
    // Convert LocalDateTime to Instant in UTC
    val instantPrep = this.toInstant(TimeZone.UTC).toEpochMilliseconds()
    val instant = Instant.fromEpochMilliseconds(instantPrep)
    println("format is valdie? ${instant.toString()}")
    return instant.toString() // This automatically formats it to ISO 8601
}
fun String.toLocalDateTimeFromISOOrNull(): LocalDateTime {
    return try {
        val instant = Instant.parse(this)
        instant.toLocalDateTime(TimeZone.UTC)
    } catch (e: Exception) {
        println("Failed to parse ISO timestamp: \"$this\" — Error: ${e.message}")
        LocalDateTime.now()
    }
}