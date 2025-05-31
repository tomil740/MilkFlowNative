package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.DemandDto
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class DemandsRemoteDao(
    private val httpClient: HttpClient
){
    suspend fun makeDemand(demand: DemandDto): Result<Unit, DataError.Network> {
        val url = "https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/Demands"

        val productItems = demand.products.joinToString(",") { product ->
            """
        {
          "mapValue": {
            "fields": {
              "productId": { "integerValue": "${product.productId}" },
              "amount": { "integerValue": "${product.amount}" },
            }
          }
        }
        """.trimIndent()
        }

        val jsonString = """
    {
      "fields": {
        "userId": { "stringValue": "${demand.userId}" },
        "distributerId": { "stringValue": "${demand.distributerId ?: ""}" },
        "status": { "stringValue": "${demand.status}" },
        "createdAt":{"timestampValue":"${demand.createdAt}"},
        "updateAt":{"timestampValue":"${demand.updatedAt}"},
        "products": {
          "arrayValue": {
            "values": [
              $productItems
            ]
          }
        }
      }
    }
    """.trimIndent()

        return try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(TextContent(jsonString, ContentType.Application.Json))
            }
            println("the rsponse ${response.status.value}")

            if (response.status.value in 200..299) {
                Result.Success(Unit)
            } else {
                when (response.status.value) {
                    401 -> Result.Error(DataError.Network.UNAUTHORIZED)
                    409 -> Result.Error(DataError.Network.CONFLICT)
                    408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                    413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                    in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                    else -> Result.Error(DataError.Network.UNKNOWN)
                }
            }
        } catch (e: UnresolvedAddressException) {
            Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: Exception) {
            println("@NetworkError: ${e.message}")
            Result.Error(DataError.Network.UNKNOWN)
        }
    }


}