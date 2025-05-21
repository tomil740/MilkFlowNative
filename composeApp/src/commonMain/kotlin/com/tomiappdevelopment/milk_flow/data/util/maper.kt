package com.tomiappdevelopment.milk_flow.data.util

import com.tomiappdevelopment.milk_flow.data.local.entities.UserEntity
import com.tomiappdevelopment.milk_flow.domain.models.User

// Extension mapping functions
fun UserEntity.toUserDomain(): User = User(
    uid = this.uid,
    name = this.name,
    distributerId = this.distributerId,
    isDistributer = this.isDistributer,
    productsCollection = this.productsCollection
)

fun User.toEntity(): UserEntity = UserEntity(
    uid = this.uid,
    name = this.name,
    distributerId = this.distributerId ?: "",
    isDistributer = this.isDistributer,
    productsCollection = this.productsCollection
)
