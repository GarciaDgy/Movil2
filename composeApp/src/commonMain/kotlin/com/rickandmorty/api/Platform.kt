package com.rickandmorty.api

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform