package org.example.project.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.api.CharacterUI
import org.example.project.api.fetchCharacter

@Composable
fun AppContent() {
    val scope = rememberCoroutineScope()
    var character by remember { mutableStateOf<CharacterUI?>(null) }
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Rick and Morty Explorer", style = MaterialTheme.typography.headlineMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("ID del personaje") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val id = inputText.toIntOrNull()
                if (id == null || id < 1) {
                    errorText = "Ingresa un ID válido (mayor a 0)"
                } else {
                    errorText = null
                    scope.launch {
                        isLoading = true
                        character = fetchCharacter(id)
                        isLoading = false
                    }
                }
            }) {
                Text("Buscar")
            }
        }

        errorText?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        character?.let {
            CharacterCard(it)
        }
    }
}
