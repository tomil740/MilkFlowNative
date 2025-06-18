package com.tomiappdevelopment.milk_flow.data.remote.core

object FirebaseConfig {
    const val PROJECT_ID = "milkflow-5c80c"
    const val DATABASE_ID = "(default)"
    const val FIRESTORE_BASE_URL = "https://firestore.googleapis.com/v1"

    val RUN_QUERY_URL: String
        get() = "$FIRESTORE_BASE_URL/projects/$PROJECT_ID/databases/$DATABASE_ID/documents:runQuery"


    object Collections {
        object Demand {
            const val COLLECTION_ID = "Demands"
            const val CREATED_AT = "createdAt"
            const val STATUS = "status"
            const val UPDATE_AT = "updateAt"
            const val USER_ID = "userId"
            const val DISTRIBUTER_ID = "distributerId"
            const val PRODUCTS = "products"

            object ProductFields {
                const val PRODUCT_ID = "productId"
                const val AMOUNT = "amount"
            }
            val POST_DEMAND_URL: String
                get() = "$FIRESTORE_BASE_URL/projects/$PROJECT_ID/databases/$DATABASE_ID/documents/$COLLECTION_ID"

            fun updateUrl(documentId: String, fieldsToUpdate: List<String>): String {
                val base = "$FIRESTORE_BASE_URL/projects/$PROJECT_ID/databases/$DATABASE_ID/documents/$COLLECTION_ID/$documentId"
                val updateMaskParams = fieldsToUpdate.joinToString("&") { "updateMask.fieldPaths=$it" }
                return "$base?$updateMaskParams"
            }
        }

        object User {
            const val COLLECTION_ID = "Users"
            const val NAME = "name"
            const val EMAIL = "email"
            const val CREATED_AT = "createdAt"
        }

        // Add more collections here as needed
    }
}
