package org.example.project.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import org.example.project.api.CharacterUI

@Composable
fun CharacterCard(character: CharacterUI) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CharacterImage(character.imageUrl)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Nombre: ${character.name}", fontWeight = FontWeight.Bold)
            Text("Especie: ${character.species}")
            Text("Estado: ${character.status}")
            Text("Origen: ${character.origin}")
        }
    }
}