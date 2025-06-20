package com.tomiappdevelopment.milk_flow.data.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import network.chaintech.utils.now


fun LocalDateTime.toLong(): Long{
    return this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

}

fun Long.toLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()) // or TimeZone.of("Asia/Jerusalem")
}

fun LocalDateTime.toISO(): String {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return instant.toString()
}

fun String.toLocalDateTimeFromISOOrNull(): LocalDateTime {
    return try {
        Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    } catch (e: Exception) {
        println("Failed to parse ISO timestamp: \"$this\" — Error: ${e.message}")
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
fun getUtcTimestamp(): String = Clock.System.now().toString()

