package com.rickandmorty.api.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.rickandmorty.api.model.RickCharacter
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun CharacterCard(character: RickCharacter) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F8E9)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            KamelImage(
                resource = asyncPainterResource(character.image),
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = character.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Species: ${character.species}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Status: ${character.status}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
