package com.tomiappdevelopment.milk_flow.di

import com.tomiappdevelopment.milk_flow.core.AuthManagerVm
import com.tomiappdevelopment.milk_flow.data.local.AuthStorageImpl
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.local.SettingsProvider
import com.tomiappdevelopment.milk_flow.data.local.dao.CartDao
import com.tomiappdevelopment.milk_flow.data.local.dao.DemandDao
import com.tomiappdevelopment.milk_flow.data.local.dao.ProductDao
import com.tomiappdevelopment.milk_flow.data.local.dao.UserDao
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.DemandsRemoteDao
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.repositories.AuthRepositoryImpl
import com.tomiappdevelopment.milk_flow.data.repositories.CartRepositoryImpl
import com.tomiappdevelopment.milk_flow.data.repositories.DemandsRepositoryImpl
import com.tomiappdevelopment.milk_flow.data.repositories.ProductRepositoryImpl
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.CartRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.DemandsRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.ProductRepository
import com.tomiappdevelopment.milk_flow.domain.usecase.GetAuthorizedProducts
import com.tomiappdevelopment.milk_flow.domain.usecase.GetConnectionState
import com.tomiappdevelopment.milk_flow.domain.usecase.GetDemandsWithUserNames
import com.tomiappdevelopment.milk_flow.domain.usecase.MakeCartDemand
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncIfNeededUseCase
import com.tomiappdevelopment.milk_flow.domain.usecase.SyncNewDemands
import com.tomiappdevelopment.milk_flow.domain.usecase.UpdateDemandsStatusUseCase
import com.tomiappdevelopment.milk_flow.presentation.CartScreen.CartScreenVm
import com.tomiappdevelopment.milk_flow.presentation.DemandItem.DemandItemVm
import com.tomiappdevelopment.milk_flow.presentation.DemandsManager.DemandsMangerVm
import com.tomiappdevelopment.milk_flow.presentation.LoginScreen.LoginViewModel
import com.tomiappdevelopment.milk_flow.presentation.core.topBar.TopBarViewModel
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

    singleOf(::ProductsRemoteDataSource)

    singleOf(::DemandsRemoteDao)

    single<ProductDao> { get<MilkFlowDb>().productDao() }

    single<UserDao> { get<MilkFlowDb>().userDao() }

    single<CartDao> { get<MilkFlowDb>().cartDao() }

    single<DemandDao> { get<MilkFlowDb>().demandDao() }

   // single<com.russhwolf.settings.Settings> { SettingsProvider.settings }

   // single { AuthStorageImpl(get()) }

    singleOf(::AuthManagerVm)

    singleOf(::AuthService)

    singleOf(::ProductRepositoryImpl).bind(ProductRepository::class)

    singleOf(::AuthRepositoryImpl).bind(AuthRepository::class)

    singleOf(::CartRepositoryImpl).bind(CartRepository::class)

    singleOf(::DemandsRepositoryImpl).bind(DemandsRepository::class)

    singleOf(::SyncIfNeededUseCase)

    singleOf(::UpdateDemandsStatusUseCase)

    singleOf(::SyncNewDemands)

    singleOf(::MakeCartDemand)

    singleOf(::GetAuthorizedProducts)

    singleOf(::GetDemandsWithUserNames)

    singleOf(::GetConnectionState)

    factoryOf(::ProductCatalogVm)

    factoryOf(::LoginViewModel)

    factoryOf(::TopBarViewModel)

    factoryOf(::CartScreenVm)

    factoryOf(::DemandsMangerVm)

    factoryOf(::DemandItemVm)

}