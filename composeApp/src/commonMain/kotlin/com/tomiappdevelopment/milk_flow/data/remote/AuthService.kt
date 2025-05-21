package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.AuthResponse
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.ProductDto
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.UserDto
import com.tomiappdevelopment.milk_flow.domain.models.User
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.domain.util.mapHttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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

    suspend fun getUserById(uid: String): Result<User, DataError> {
        val url = "https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/users/$uid"

        val response = try {
            client.get(url)
        } catch (e: UnresolvedAddressException) {
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(DataError.Network.UNKNOWN)
        }

        return when (response.status.value) {
            in 200..299 -> {
                try {
                    val json = response.body<JsonObject>()

                    // Firestore document fields are nested inside "fields" object
                    val fields = json["fields"]?.jsonObject ?: return Result.Error(DataError.Network.SERIALIZATION)

                    // Extract fields safely
                    val user = User(
                        uid = fields["uid"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: "",
                        name = fields["name"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: "",
                        distributerId = fields["distributerId"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: "",
                        isDistributer = fields["isDistributer"]?.jsonObject?.get("booleanValue")?.jsonPrimitive?.content.toBoolean() ?: false,
                        productsCollection = fields["productsCollection"]?.jsonObject
                            ?.get("arrayValue")?.jsonObject
                            ?.get("values")?.jsonArray
                            ?.mapNotNull { valueItem ->
                                valueItem.jsonObject["integerValue"]?.jsonPrimitive?.content?.toIntOrNull()
                            } ?: emptyList()
                    )

                    Result.Success(user)
                } catch (e: Exception) {
                    Result.Error(DataError.Network.SERIALIZATION)
                }
            }

            401 -> Result.Error(DataError.Network.UNAUTHORIZED)
            409 -> Result.Error(DataError.Network.CONFLICT)
            408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
            413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
            else -> Result.Error(DataError.Network.UNKNOWN)
        }
    }


}

