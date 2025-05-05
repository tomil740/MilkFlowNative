package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.ProductRepositoryImpl
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogVm
import org.koin.dsl.module


val appModule = module {
    single<ProductRepository> { ProductRepositoryImpl() }

    factory { ProductCatalogVm(get()) }

}