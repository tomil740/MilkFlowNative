package com.tomiappdevelopment.milk_flow

import platform.Foundation.NSBundle


actual object AppConfig {
    actual val firebaseApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("FIREBASE_API_KEY") as String

}

