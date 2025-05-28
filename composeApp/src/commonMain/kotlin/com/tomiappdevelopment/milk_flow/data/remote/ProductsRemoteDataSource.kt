package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.ProductDto
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class ProductsRemoteDataSource(
    private val httpClient: HttpClient
){

    suspend fun getAllProducts(): Result<List<ProductDto>, DataError.Network> {
        val allProducts = mutableListOf<ProductDto>()
        var pageToken: String? = null

        do {
            val url = buildString {
                append("https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/products")
                if (pageToken != null) append("?pageToken=$pageToken")
            }

            val response = try {
                httpClient.get(url)
            } catch (e: UnresolvedAddressException) {
                return Result.Error(DataError.Network.NO_INTERNET)
            } catch (e: SerializationException) {
                return Result.Error(DataError.Network.SERIALIZATION)
            } catch (e: Exception) {
                return Result.Error(DataError.Network.UNKNOWN)
            }

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

            val json = response.body<JsonObject>()
            val documents = json["documents"]?.jsonArray ?: JsonArray(emptyList())
            pageToken = json["nextPageToken"]?.jsonPrimitive?.contentOrNull

            val products = documents.mapNotNull { doc ->
                try {
                    val fields = doc.jsonObject["fields"]?.jsonObject ?: return@mapNotNull null

                    ProductDto(
                        id = fields["id"]?.jsonObject?.get("integerValue")?.jsonPrimitive?.intOrNull ?: 0,
                        category = fields["category"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty(),
                        itemsPerPackage = fields["itemsPerPackage"]?.jsonObject?.get("integerValue")?.jsonPrimitive?.intOrNull ?: 0,
                        imgKey = fields["imgKey"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty(),
                        name = fields["name"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty(),
                        description = fields["description"]?.jsonObject?.get("stringValue")?.jsonPrimitive?.content.orEmpty(),
                        weight = fields["weight"]?.jsonObject?.get("integerValue")?.jsonPrimitive?.intOrNull ?: 0,
                        barcode = fields["barcode"]?.jsonObject?.get("integerValue")?.jsonPrimitive?.longOrNull ?: 0L
                    )
                } catch (e: Exception) {
                    println("@ParseError $e")
                    null
                }
            }

            allProducts += products

        } while (pageToken != null)

        return Result.Success(allProducts)
    }


    suspend fun getProductsMetadata(): Result<Long, DataError.Network> {
        val response = try {
            httpClient.get("https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/metadata/productSync") {

            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(DataError.Network.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val json = response.body<JsonObject>()
                println("the obj $json")
                val syncTimestamp = json["fields"]?.jsonObject
                    ?.get("updateAt")?.jsonObject
                    ?.get("timestampValue")?.jsonPrimitive?.contentOrNull

                if (syncTimestamp != null) Result.Success(Instant.parse(syncTimestamp).toEpochMilliseconds())
                else Result.Error(DataError.Network.SERIALIZATION)
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