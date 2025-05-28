package com.tomiappdevelopment.milk_flow.domain.usecase

import com.tomiappdevelopment.milk_flow.core.AuthManager
import com.tomiappdevelopment.milk_flow.domain.models.Product
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAuthorizedProducts(
    private val authManager: AuthManager,
    private val productsRepo: ProductRepository
) {
    operator fun invoke(scope: CoroutineScope): Flow<List<Product>> {
        return combine(
          authManager.userFlow(scope),
            productsRepo.getProducts()
        ) { user, products ->
            if (products.isEmpty()) return@combine emptyList()

            when {
                user?.isDistributer == true -> products
                user != null -> products.filter { it.id in user.productsCollection }
                else -> emptyList()
            }
        }
    }
}