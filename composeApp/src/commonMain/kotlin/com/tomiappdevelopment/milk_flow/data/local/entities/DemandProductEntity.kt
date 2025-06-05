package com.tomiappdevelopment.milk_flow.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.tomiappdevelopment.milk_flow.data.util.toLong
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.Demand

@Entity(tableName = "demands")
data class DemandEntity(
    @PrimaryKey val demandId: String,
    val uid: String,
    val distributerId: String?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "demand_products",
    primaryKeys = ["demandId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = DemandEntity::class,
            parentColumns = ["demandId"],
            childColumns = ["demandId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("demandId")]
)
data class DemandProductEntity(
    val demandId: String,
    val productId: Int,
    val amount: Int,
)

data class DemandWithProductsE(
    @Embedded val demand: DemandEntity,
    @Relation(
        parentColumn = "demandId",
        entityColumn = "demandId"
    )
    val products: List<DemandProductEntity>
)



//mapers

fun Demand.toDemandEntity(): DemandEntity{
    return DemandEntity(
        demandId = id,
        userId,
        distributerId,
        status.toString(),
        createdAt = this.createdAt.toLong(),
        updatedAt = this.updatedAt.toLong(),
    )
}
fun CartItem.toDemandProductEntity(demandId: String): DemandProductEntity {
    return DemandProductEntity(
        demandId = demandId,
        productId = this.productId,
        amount = this.amount
    )
}
fun Demand.toDemandWithProductsE(): DemandWithProductsE {
    // Map DemandEntity first
    val demandEntity = this.toDemandEntity()

    // Convert CartItems to DemandProductEntities
    val demandProductEntities = this.products.map { it.toDemandProductEntity(demandId = this.id) }

    // Return the full DemandWithProductsE
    return DemandWithProductsE(
        demand = demandEntity,
        products = demandProductEntities
    )
}





