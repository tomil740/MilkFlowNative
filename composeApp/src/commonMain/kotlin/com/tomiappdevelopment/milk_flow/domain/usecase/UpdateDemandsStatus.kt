package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.models.subModels.DemandStatusUpdateEntry
import com.tomiappdevelopment.milk_flow.domain.models.subModels.UpdateDemandsStatusParams
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.util.DemandError
import com.tomiappdevelopment.milk_flow.domain.util.Result

class UpdateDemandsStatusUseCase(
    private val demandsRepo: DemandsRepository
) {

    suspend operator fun invoke(
        params: UpdateDemandsStatusParams,
        authState: User?,

        ): Result<Boolean,  DemandError> {
        // ✅ Step 1: Validate user auth
        val user = authState
            ?: return Result.Error(DemandError.NotAuthenticated)
        if (!user.isDistributer) {
            return Result.Error((DemandError.DistributerNotAllowed))
        }

        // ✅ Step 2: Validate data
        if (params.demands.isEmpty()) {
            return Result.Error(DemandError.EmptyCart)
        }

        // ✅ Step 3: Validate allowed status transitions
        val invalidStatus = params.demands.any {
            it.status !in listOf(Status.pending, Status.placed)
        }
        if (invalidStatus) {
            return Result.Error(DemandError.InvalidStatus)
        }
        // ✅ Step 4: Prepare data for remote update
        val updateList1 = params.demands.map { demand ->
            demand.id
        }
        val updateList = DemandStatusUpdateEntry(updateList1,params.targetStatus)

        // ✅ Step 5: Try update via remote DAO
        return try {
            demandsRepo.updateDemandsStatus(updateList)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(DemandError.Unknown)
        }
    }
}














