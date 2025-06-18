package com.tomiappdevelopment.milk_flow.data.remote.core

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

suspend fun isOnline(httpClient: HttpClient): Boolean {
    return try {
            val response: HttpResponse = httpClient.get("https://clients3.google.com/generate_204") {
                headers {
                    append(HttpHeaders.CacheControl, "no-cache")
                }
            }
            response.status == HttpStatusCode.NoContent

    } catch (e: Exception) {
        false
    }
}
