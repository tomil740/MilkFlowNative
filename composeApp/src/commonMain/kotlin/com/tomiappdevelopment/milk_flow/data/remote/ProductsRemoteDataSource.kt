package com.tomiappdevelopment.milk_flow.data.remote

import com.tomiappdevelopment.milk_flow.data.remote.dtoModels.ProductDto
import com.tomiappdevelopment.milk_flow.domain.util.DataError
import com.tomiappdevelopment.milk_flow.domain.util.DataException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.code
import dev.gitlive.firebase.firestore.firestore
import kotlinx.io.IOException


class ProductsRemoteDataSource{

    private val firestore = Firebase.firestore

    suspend fun getAllProducts(): List<ProductDto> {
        try {
            val snapshot = firestore.collection("products").get()

            //my temp fix
            if (snapshot.documents.isEmpty()){
                throw DataException(DataError.Network.NO_INTERNET)
            }

            return snapshot.documents.mapNotNull { it.toProductDto() }
        } catch (e: IOException) {
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
            throw DataException(DataError.Network.UNKNOWN)
        }
    }


    suspend fun getProductsMetadata(): Long {
        val doc = firestore
            .collection("metadata")
            .document("productSync")
            .get()

        // Check if document exists
        if (doc.exists) {
            // Retrieve the "updatedAt" field as a Timestamp
            val updatedAt = doc.get("updateAt") as? Timestamp

            // Check if updatedAt is valid and convert to milliseconds
            return updatedAt?.seconds?.times(1000) ?: 0L
        } else {
            // Return 0L if the document does not exist
            return 0L
        }
    }

    private fun DocumentSnapshot.toProductDto(): ProductDto? {
        val id = this.get("id") as? Int ?: return null
        val barcode = this.get("barcode") as? String ?: return null
        val name = this.get("name") as? String ?: return null
        val imageUrl = this.get("imageUrl") as? String ?: ""
        val category = this.get("category") as? String ?: "Uncategorized"
        val itemsPerPackage = (this.get("itemsPerPackage") as? Long)?.toInt() ?: 0

        return ProductDto(
            id = id,
            barcode = barcode,
            name = name,
            imageUrl = imageUrl,
            category = category,
            itemsPerPackage = itemsPerPackage
        )
    }

}