package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.mapHttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthService(
    private val client: HttpClient,
    private val firebaseApiKey: String
)  {

    suspend fun signIn(email: String, password: String): Result<AuthResponse, DataError> {

        return try {
            println(":Starting teh api key $firebaseApiKey")
            val jsonString = """
    {
      "email": "$email",
      "password": "$password",
      "returnSecureToken": true
    }
""".trimIndent()

            val response = client.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword") {
                parameter("key", firebaseApiKey)
                contentType(ContentType.Application.Json)
                setBody(TextContent(jsonString, ContentType.Application.Json))
            }
            println("!@@@@#@!#$@#$@#$")
            println(response.status)

            val jsonText = response.bodyAsText()
            val json = Json.parseToJsonElement(jsonText).jsonObject

            val idToken = json["idToken"]?.jsonPrimitive?.contentOrNull
            val refreshToken = json["refreshToken"]?.jsonPrimitive?.contentOrNull
            val localId = json["localId"]?.jsonPrimitive?.contentOrNull

            if (idToken != null && refreshToken != null && localId != null) {
                val authResponse = AuthResponse(idToken, refreshToken, localId)
                Result.Success(authResponse)

   } else {
                // Optionally parse Firebase error here from json["error"]
                Result.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(mapHttpResponse(e))
        }
    }
}

