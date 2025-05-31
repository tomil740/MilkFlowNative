package com.tomiappdevelopment.milk_flow.data.remote.dtoModels

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Demand
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import network.chaintech.utils.now

data class DemandDto(
    val userId: String,
    val distributerId: String?, // Nullable String for distributerId
    val status: Status, // Enum for status
    val createdAt: String = LocalDateTime.now().toISO(),
    val updatedAt: String = LocalDateTime.now().toISO(),
    val products: List<CartItem> // List of CartItem
)

fun LocalDateTime.toISO(): String {
    // Convert LocalDateTime to Instant in UTC
    val instant = this.toInstant(TimeZone.UTC)
    return instant.toString() // This automatically formats it to ISO 8601
}

fun Demand.toDemandDto(): DemandDto{
    return DemandDto(
         userId,
         distributerId,
         status,
         createdAt = this.createdAt.toISO(),
         updatedAt = this.updatedAt.toISO(),
         products
    )
}

