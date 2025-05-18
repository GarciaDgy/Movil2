package org.example.project.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private val client = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
}

suspend fun fetchCharacter(id: Int): CharacterUI {
    return try {
        val response: CharacterResponse =
            client.get("https://rickandmortyapi.com/api/character/$id").body()
        CharacterUI(
            id = response.id,
            name = response.name,
            imageUrl = response.image,
            status = response.status,
            species = response.species,
            origin = response.origin.name
        )
    } catch (e: Exception) {
        e.printStackTrace()
        CharacterUI(
            id = id,
            name = "desconocido",
            imageUrl = "",
            status = "desconocido",
            species = "desconocido",
            origin = "desconocido"
        )
    }
}
