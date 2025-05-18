package org.example.project.compose

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import java.net.URL
import javax.imageio.ImageIO

@Composable
actual fun CharacterImage(imageUrl: String) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        runCatching {
            val bufferedImage = ImageIO.read(URL(imageUrl))
            bufferedImage.toComposeImageBitmap()
        }.onSuccess {
            imageBitmap = it
        }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Character image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
        )
    }
}