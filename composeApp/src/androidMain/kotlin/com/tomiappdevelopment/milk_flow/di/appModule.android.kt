package com.tomiappdevelopment.milk_flow.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.tomiappdevelopment.milk_flow.BuildConfig
import com.tomiappdevelopment.milk_flow.core.notifications.NotificationSender
import com.tomiappdevelopment.milk_flow.core.workers.DemandsStatusNotificationWorker
import com.tomiappdevelopment.milk_flow.data.core.ConnectionObserverImpl
import com.tomiappdevelopment.milk_flow.data.local.AuthStorageImplAndroid
import com.tomiappdevelopment.milk_flow.data.local.DatabaseFactory
import com.tomiappdevelopment.milk_flow.data.local.MilkFlowDb
import com.tomiappdevelopment.milk_flow.data.remote.AuthService
import com.tomiappdevelopment.milk_flow.data.remote.ProductsRemoteDataSource
import com.tomiappdevelopment.milk_flow.data.remote.createHttpClient
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthRepository
import com.tomiappdevelopment.milk_flow.domain.repositories.AuthStorage
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule() = module {

    val apiKey = BuildConfig.FIREBASE_API_KEY

    single<MilkFlowDb> { DatabaseFactory(get()).create() }

    single<HttpClient> {createHttpClient(OkHttp.create()) }

    single<ProductsRemoteDataSource> {ProductsRemoteDataSource(get()) }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { get<Context>().preferencesDataStoreFile("auth_prefs") }
        )
    }

    single { AuthStorageImplAndroid(get()) }.bind(AuthStorage::class)

    single<AuthService> {AuthService(client = get(), firebaseApiKey = apiKey) }

    single<ConnectionObserver> { ConnectionObserverImpl(get()) }

    single<NotificationSender> { NotificationSender(get()) }

    worker<DemandsStatusNotificationWorker>{DemandsStatusNotificationWorker(get(),get())}


}