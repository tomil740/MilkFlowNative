package com.tomiappdevelopment.milk_flow.domain.models.subModels

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.Demand

data class UpdateDemandsStatusParams(
    val demands: List<Demand>,
    val targetStatus: Status
)

data class DemandStatusUpdateEntry(
    val demandId: List<String>,
    val newStatus: Status
)