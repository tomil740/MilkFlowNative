package com.tomiappdevelopment.milk_flow.data.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectionObserverImpl(context: Context) : ConnectionObserver {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val connectionState: Flow<ConnectionState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(ConnectionState.Available)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(ConnectionState.Lost)
            }
            override fun onUnavailable() {
                super.onUnavailable()
                trySend(ConnectionState.Unavailable)
            }
        }
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

}
