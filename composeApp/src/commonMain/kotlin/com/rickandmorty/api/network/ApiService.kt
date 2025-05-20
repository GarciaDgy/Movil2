package com.rickandmorty.api.network

import com.rickandmorty.api.model.CharacterResponse
import com.rickandmorty.api.model.RickCharacter
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

suspend fun getCharacters(page: Int = 1): List<RickCharacter> {
    val response: CharacterResponse =
        httpClient.get("https://rickandmortyapi.com/api/character/?page=$page").body()
    return response.results
}
