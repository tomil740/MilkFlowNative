package com.tomiappdevelopment.milk_flow.domain.models

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.toISO
import com.tomiappdevelopment.milk_flow.domain.core.Status
import kotlinx.datetime.LocalDateTime
import network.chaintech.utils.now

// Base Demand class
open class Demand(
    val id: String,
    val userId: String,
    val distributerId: String?,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val products: List<CartItem>
)

data class DemandWithNames(
    val base: Demand,
    val userName: String,
    val distributerName: String?
) {
    val id = base.id
    val userId = base.userId
    val distributerId = base.distributerId
    val status = base.status
    val createdAt = base.createdAt
    val updatedAt = base.updatedAt
    val products = base.products
}

data class DemandsWithNextPageToken(
    val demands: List<Demand>,
    val nextPageToken: String?
)