package com.uncaan.mengaji

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform