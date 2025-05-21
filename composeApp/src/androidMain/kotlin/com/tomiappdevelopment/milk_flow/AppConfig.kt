package com.tomiappdevelopment.milk_flow



actual object AppConfig {
    actual val firebaseApiKey: String
        get() = BuildConfig.FIREBASE_API_KEY

}
