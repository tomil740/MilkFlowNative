package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.core.AuthManagerVm
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAuthorizedProducts(
    private val authManagerVm: AuthManagerVm,
    private val productsRepo: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return combine(
            authManagerVm.userState, // Now it's a StateFlow<User?>
            productsRepo.getProducts()
        ) { user, products ->
            println("the full ${products.size}")
            if (products.isEmpty()) return@combine emptyList()

            when {
                user?.isDistributer == true -> products
                user != null -> products.filter { it.id in user.productsCollection }
                else -> emptyList()
            }
        }
    }
}
