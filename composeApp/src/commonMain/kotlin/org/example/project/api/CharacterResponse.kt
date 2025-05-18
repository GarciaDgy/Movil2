package org.example.project.api
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class CharacterResponse(
    val id: Int,
    val name: String,
    val status:String,
    val species: String,
    val image: String,
    val origin: OriginResponse
)

@Serializable
data class OriginResponse(
    val name: String
)
