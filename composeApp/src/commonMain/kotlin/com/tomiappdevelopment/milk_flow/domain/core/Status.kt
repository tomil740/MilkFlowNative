package com.tomiappdevelopment.milk_flow.domain.core

enum class Status {
    pending,
    placed,
    completed
}
fun Status.getNextStatus(): Status? = when (this) {
    Status.pending -> Status.placed
    Status.placed -> Status.completed
    Status.completed -> null // No next status
}

fun Status.getStringName():String{
    return when (this) {
        Status.placed -> "שודר"
        Status.completed -> "סופק"
        Status.pending -> "ממתין" // No next status
    }
}