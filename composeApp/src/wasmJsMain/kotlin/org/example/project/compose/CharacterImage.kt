package org.example.project.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.css.*
import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.AttrsBuilder

@Composable
actual fun CharacterImage(imageUrl: String) {
    Img(
        src = imageUrl,
        attrs = {
            style {
                width(200.px)
                height(200.px)
                property("object-fit", "cover")
                borderRadius(12.px)
            }
        }
    )
}