package com.rickandmorty.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rickandmorty.api.model.RickCharacter
import com.rickandmorty.api.network.getCharacters
import com.rickandmorty.api.ui.CharacterCard
import kotlinx.coroutines.launch

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var characters by remember { mutableStateOf<List<RickCharacter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var allCharacters by remember { mutableStateOf<List<RickCharacter>>(emptyList()) }

    LaunchedEffect(true) {
        isLoading = true
        val result = getCharacters() // página 1
        allCharacters = result
        characters = result
        isLoading = false
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF00ACC1),
            secondary = Color(0xFFB2EBF2),
            background = Color(0xFFE0F7FA),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // 👈 scroll completo
                    .padding(16.dp)
            ) {
                Text(
                    text = "Rick and Morty (${getPlatform().name})",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        characters = allCharacters.filter { character ->
                            character.name.contains(searchQuery, ignoreCase = true)
                        }
                    },
                    label = { Text("Buscar personaje") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                characters.forEach { character ->
                    CharacterCard(character = character)
                }

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
