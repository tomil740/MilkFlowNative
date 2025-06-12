package com.tomiappdevelopment.milk_flow.data.core

import com.tomiappdevelopment.milk_flow.domain.core.ConnectionState
import com.tomiappdevelopment.milk_flow.domain.repositories.ConnectionObserver
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ConnectionObserverImpl(
    private val client: HttpClient
) : ConnectionObserver {
    private val stateFlow = MutableStateFlow(ConnectionState.Unavailable)

    private suspend fun checkConnection() {
        try {
            val response = client.get("https://clients3.google.com/generate_204") {
                timeout { requestTimeoutMillis = 1500 }
            }
            val connected = response.status.value == 204
            stateFlow.emit(if (connected) ConnectionState.Available else ConnectionState.Unavailable)
        } catch (_: Exception) {
            stateFlow.emit(ConnectionState.Unavailable)
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                checkConnection()
                delay(5000)
            }
        }
    }

    override val connectionState: Flow<ConnectionState> = stateFlow
}
