package com.tomiappdevelopment.milk_flow.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.TimeZone

// Helper function to convert LocalDateTime to Long (milliseconds since epoch)
fun localDateTimeToMillis(localDateTime: LocalDateTime): Long {
    return localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
}
