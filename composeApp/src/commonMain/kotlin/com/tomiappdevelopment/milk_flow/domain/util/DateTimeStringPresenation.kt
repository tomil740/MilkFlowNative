package com.tomiappdevelopment.milk_flow.domain.util

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.toReadableString(): String {
    val padded = { value: Int -> value.toString().padStart(2, '0') }
    return "${padded(dayOfMonth)}/${padded(monthNumber)}/$year ${padded(hour)}:${padded(minute)}"
}

