package org.example.project.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
actual fun CharacterImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Character image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    )
}
