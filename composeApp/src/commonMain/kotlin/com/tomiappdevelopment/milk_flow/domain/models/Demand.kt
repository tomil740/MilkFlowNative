package com.tomiappdevelopment.milk_flow.domain.models

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.toISO
import com.tomiappdevelopment.milk_flow.domain.core.Status
import kotlinx.datetime.LocalDateTime
import network.chaintech.utils.now

data class Demand(
    val userId: String,
    val distributerId: String?,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val products: List<CartItem>
)
