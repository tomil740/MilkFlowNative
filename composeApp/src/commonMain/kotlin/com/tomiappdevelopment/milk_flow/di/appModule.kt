package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.data.ProductRepositoryImpl
import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.presentation.productCatalog.ProductCatalogVm
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun platformModule(): Module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            appModule,platformModule()
        )
    }

val appModule = module {
    
    single<ProductDao> { get<MilkFlowDb>().productDao() }

    singleOf(::ProductRepositoryImpl).bind(ProductRepository::class)

    factoryOf(::ProductCatalogVm)
}