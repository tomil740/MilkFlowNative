package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.ProductDto
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DataException
import com.tomiappdevelopment.milk_flow.domain.util.Result
import com.tomiappdevelopment.milk_flow.getProductName
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.code
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Instant
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
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

    private val firestore = Firebase.firestore

    suspend fun getAllProducts1(): List<ProductDto> {
        try {

            val snapshot1 = firestore.collection("products").document("53746").get()
            println("@@@@@@@THIS ${snapshot1.id}")

            println("@@@@@@@THIS Try!!!!")

            if (snapshot1.exists) {
               // println("the val@@!$@#${snapshot1.get("updateAt") as? TimestampEncoder}")

                val imgKey  = runCatching { snapshot1.get<Int>("itemsPerPackage") }.getOrNull() ?: ""
                val imgKey1  = runCatching { snapshot1.get<Long>("itemsPerPackage") }.getOrNull() ?: ""
                val imgKey3  = runCatching { snapshot1.getProductName() }.getOrNull() ?: ""

                println("isnt working!!!?????? $imgKey")
                println("the val999999@@!$@#${imgKey1}")
                println("the val999999@@!$@#${imgKey3}")


            }


            val snapshot = firestore.collection("products").get()

            //my temp fix
            if (snapshot.documents.isEmpty()){
                throw DataException(DataError.Network.NO_INTERNET)
            }

            //print("working@@@@@@@@@@ ${snapshot.documents.toString()}")
            val products = snapshot.documents.map { doc ->
                if (doc.exists){
                   // print("working@@@@@@@@@@ ${doc.id}")
                   // print("working@@@@@@@@@@ ${firestore.collection("products").document(doc.id).get()}")
                }

            }
              //  doc.data<ProductDto>()
           // }
            return listOf()
        } catch (e: IOException) {
            println("isnt working!!!?????? $e")

            throw DataException(DataError.Network.NO_INTERNET)
        } catch (e: FirebaseFirestoreException) {
            val code = e.code.name.lowercase()

            val error = when {
                "permission_denied" in code -> DataError.Network.UNAUTHORIZED
                "unavailable" in code -> DataError.Network.SERVER_ERROR
                "deadline_exceeded" in code || "timeout" in code -> DataError.Network.REQUEST_TIMEOUT
                else -> DataError.Network.UNKNOWN
            }

            throw DataException(error)
        } catch (e: Exception) {
            println("isnt working!!!?????? $e")
            throw DataException(DataError.Network.UNKNOWN)
        }
    }

    suspend fun getAllProducts(): Result<List<ProductDto>, DataError.Network> {
        val response = try {
            httpClient.get("https://firestore.googleapis.com/v1/projects/milkflow-5c80c/databases/(default)/documents/products")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(DataError.Network.SERIALIZATION)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val json = response.body<JsonObject>()
                val documents = json["documents"]?.jsonArray ?: return Result.Success(emptyList())

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
                        null // Skip malformed items
                    }
                }

                Result.Success(products)
            }

            401 -> Result.Error(DataError.Network.UNAUTHORIZED)
            409 -> Result.Error(DataError.Network.CONFLICT)
            408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
            413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
            else -> Result.Error(DataError.Network.UNKNOWN)
        }
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