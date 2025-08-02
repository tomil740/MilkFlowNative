package com.tomiappdevelopment.milk_flow.domain.core

enum class Status {
    pending,
    placed,
    completed,
    deleted
}

fun Status.getNextStatus(): Status? = when (this) {
    Status.pending -> Status.placed
    Status.placed -> Status.completed
    Status.completed, Status.deleted -> null
}

fun Status.getStringName(): String = when (this) {
    Status.pending -> "ממתין"
    Status.placed -> "שודר"
    Status.completed -> "סופק"
    Status.deleted -> "נמחק"
}
