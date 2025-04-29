package com.tomiappdevelopment.milk_flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform