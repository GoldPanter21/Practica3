package com.gmail.zyanyasanchezv.cammickmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform