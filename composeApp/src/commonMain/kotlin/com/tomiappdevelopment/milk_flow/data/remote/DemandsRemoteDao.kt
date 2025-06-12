package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.DemandDto
import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.PagedDemandsDto
import com.tomiappdevelopment.milk_flow.data.util.toISO
import com.tomiappdevelopment.milk_flow.domain.core.Status
import com.tomiappdevelopment.milk_flow.domain.models.CartItem
import com.tomiappdevelopment.milk_flow.domain.models.subModels.DemandStatusUpdateEntry
import com.tomiappdevelopment.milk_flow.domain.models.subModels.UpdateDemandsStatusParams
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import network.chaintech.utils.now
import kotlin.time.Duration.Companion.hours

class DemandsRemoteDao(
    private val httpClient: HttpClient
) {

    suspend fun getDemandsPage(
        startAfterTimestamp: String? = null,
    ): Result<PagedDemandsDto, DataError.Network> {
        val now = Clock.System.now()
        val thresholdInstant = now.minus(72.hours)
        val thresholdTimestamp = thresholdInstant.toLocalDateTime(TimeZone.UTC).toString() + "Z"

        val limit = if(startAfterTimestamp!=null){50}else{2}

        val structuredQuery = buildJsonObject {
            putJsonObject("structuredQuery") {
                putJsonArray("from") {
                    add(buildJsonObject { put("collectionId", "Demands") })
                }
                putJsonObject("where") {
                    putJsonObject("compositeFilter") {
                        put("op", "AND")
                        putJsonArray("filters") {
                            // Filter by createdAt >= timestamp
                            addJsonObject {
                                putJsonObject("fieldFilter") {
                                    putJsonObject("field") { put("fieldPath", "createdAt") }
                                    put("op", "GREATER_THAN_OR_EQUAL")
                                    putJsonObject("value") { put("timestampValue", thresholdTimestamp) }
                                }
                            }
/*
                            addJsonObject {
                                putJsonObject("fieldFilter") {
                                    putJsonObject("field") { put("fieldPath", "distributerId") }
                                    put("op", "EQUAL")
                                    putJsonObject("value") { put("stringValue", uid) }
                                }
                            }

 */

                        }
                    }
                }

                putJsonArray("orderBy") {
                    addJsonObject {
                        putJsonObject("field") { put("fieldPath", "updateAt") }
                        put("direction", "DESCENDING")
                    }
                }
/*
                if (startAfterTimestamp != null) {
                    putJsonArray("startAfter") {
                        add(JsonPrimitive(startAfterTimestamp)) // ✅ Raw timestamp string
                    }
                }

 */

                put("limit", limit) // Always include
            }
        }

        val url =
            "https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents:runQuery"

        val jsonBodyString = structuredQuery.toString()

        val response = try {
            httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(TextContent(jsonBodyString, ContentType.Application.Json))
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(DataError.Network.UNKNOWN)
        }
        println("Response ${response.status.value}")

        if (response.status.value !in 200..299) {
            return when (response.status.value) {
                401 -> Result.Error(DataError.Network.UNAUTHORIZED)
                409 -> Result.Error(DataError.Network.CONFLICT)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        val responseBody = response.body<String>()
        val demands = mutableListOf<DemandDto>()


        try {

            var lastCreatedAtCursor: String? = null

            val rootElement = Json.parseToJsonElement(responseBody)
            if (rootElement is JsonArray) {
                for (docElement in rootElement) {
                    if (docElement is JsonObject) {
                        val docObj = docElement["document"]?.jsonObject ?: continue

                        val fullName = docObj["name"]?.jsonPrimitive?.content.orEmpty()
                        val id = fullName.substringAfterLast("/")

                        val fields = docObj["fields"]?.jsonObject ?: continue

                        val userId =
                            fields["userId"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty()
                        val distributerId =
                            fields["distributerId"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.contentOrNull
                        val statusStr =
                            fields["status"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty()
                        val createdAt =
                            fields["createdAt"]?.jsonObject?.get("timestampValue")?.jsonPrimitive?.content.orEmpty()
                        val updatedAt =
                            fields["updateAt"]?.jsonObject?.get("timestampValue")?.jsonPrimitive?.content.orEmpty()

                        val productValues = fields["products"]
                            ?.jsonObject?.get("arrayValue")
                            ?.jsonObject?.get("values")
                            ?.jsonArray ?: JsonArray(emptyList())

                        val products = productValues.mapNotNull { item ->
                            try {
                                val itemFields = item.jsonObject["mapValue"]
                                    ?.jsonObject?.get("fields")
                                    ?.jsonObject ?: return@mapNotNull null

                                val productId = itemFields["productId"]
                                    ?.jsonObject?.get("integerValue")
                                    ?.jsonPrimitive?.intOrNull ?: 0

                                val quantity = itemFields["amount"]
                                    ?.jsonObject?.get("integerValue")
                                    ?.jsonPrimitive?.intOrNull ?: 0

                                CartItem(productId = productId, amount = quantity)
                            } catch (e: Exception) {
                                println("@CartItemParseError $e")
                                null
                            }
                        }

                        demands.add(
                            DemandDto(
                                id = id,
                                userId = userId,
                                distributerId = distributerId,
                                status = Status.valueOf(statusStr),
                                createdAt = createdAt,
                                updatedAt = updatedAt,
                                products = products
                            )
                        )

                        if (createdAt.isNotEmpty()) lastCreatedAtCursor = createdAt
                    }
                }
            }

            val nextPageCursor = if (demands.size == limit) lastCreatedAtCursor else null

            return Result.Success(PagedDemandsDto(demands, nextPageCursor))

        } catch (e: Exception) {
            println("Fail?:$e ")

            println("@DemandParseError $e")
            return Result.Error(DataError.Network.UNKNOWN)
        }
    }





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

    suspend fun updateDemandsStatus(params: DemandStatusUpdateEntry): Result<Unit, DataError.Network> {
        for (demand in params.demandId) {
            val documentId = demand ?: return Result.Error(DataError.Network.UNKNOWN)

            val url =
                "https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/Demands/$documentId?updateMask.fieldPaths=status&updateMask.fieldPaths=updateAt"

            val jsonString = """
            {
              "fields": {
                "status": { "stringValue": "${params.newStatus.name}" },
                "updateAt": { "timestampValue":"${LocalDateTime.now().toISO()}" }
              }
            }
        """.trimIndent()

            try {
                val response = httpClient.patch(url) {
                    contentType(ContentType.Application.Json)
                    setBody(TextContent(jsonString, ContentType.Application.Json))
                }
                println("ResponseCode ${response.status}")

                if (response.status.value !in 200..299) {
                    return when (response.status.value) {
                        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
                        409 -> Result.Error(DataError.Network.CONFLICT)
                        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                        404 -> Result.Error(DataError.Network.NOT_FOUND)
                        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                        else -> Result.Error(DataError.Network.UNKNOWN)
                    }
                }

            } catch (e: UnresolvedAddressException) {
                return Result.Error(DataError.Network.NO_INTERNET)
            } catch (e: Exception) {
                println("@NetworkError: ${e.message}")
                return Result.Error(DataError.Network.UNKNOWN)
            }
        }

        return Result.Success(Unit)
    }



}