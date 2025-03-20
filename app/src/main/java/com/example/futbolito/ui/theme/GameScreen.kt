package com.example.futbolito.ui.theme


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GameScreen() {
    val contexto = LocalContext.current
    val sensorManager = remember { contexto.getSystemService(SensorManager::class.java) }
    val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var posXPelota by remember { mutableStateOf(0f) }
    var posYPelota by remember { mutableStateOf(0f) }
    val radioPelota = 20f

    var anchoPantalla by remember { mutableStateOf(0f) }
    var altoPantalla by remember { mutableStateOf(0f) }

    val margenCampo = 30f
    val anchoPorteriaFactor = 0.4f
    val altoPorteriaFactor = 0.07f
    val grosorBorde = 8f

    var marcadorSuperior by remember { mutableStateOf(0) }
    var marcadorInferior by remember { mutableStateOf(0) }

    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent?) {
                evento?.let {
                    val movimientoX = it.values[0]
                    val movimientoY = it.values[1]

                    val minX = margenCampo + grosorBorde + radioPelota
                    val maxX = anchoPantalla - margenCampo - grosorBorde - radioPelota
                    val minY = margenCampo + grosorBorde + radioPelota
                    val maxY = altoPantalla - margenCampo - grosorBorde - radioPelota

                    posXPelota = max(minX, min(maxX, posXPelota - movimientoX * 5))
                    posYPelota = max(minY, min(maxY, posYPelota + movimientoY * 5))

                    val anchoCampo = anchoPantalla - 2 * margenCampo
                    val anchoPorteria = anchoCampo * anchoPorteriaFactor
                    val altoPorteria = altoPantalla * altoPorteriaFactor
                    val posXPorteria = margenCampo + (anchoCampo - anchoPorteria) / 2

                    if (posYPelota - radioPelota <= margenCampo + altoPorteria &&
                        posXPelota in posXPorteria..(posXPorteria + anchoPorteria)) {
                        marcadorInferior++
                        posXPelota = anchoPantalla / 2
                        posYPelota = altoPantalla / 2
                    }

                    if (posYPelota + radioPelota >= altoPantalla - margenCampo - altoPorteria &&
                        posXPelota in posXPorteria..(posXPorteria + anchoPorteria)) {
                        marcadorSuperior++
                        posXPelota = anchoPantalla / 2
                        posYPelota = altoPantalla / 2
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) {}
        }
    }

    DisposableEffect(sensorManager) {
        sensorManager.registerListener(
            sensorListener,
            acelerometro,
            SensorManager.SENSOR_DELAY_GAME
        )
        onDispose { sensorManager.unregisterListener(sensorListener) }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Marcador superior: $marcadorSuperior", modifier = Modifier.padding(16.dp))
            Text("Marcador inferior: $marcadorInferior", modifier = Modifier.padding(16.dp))
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                anchoPantalla = size.width
                altoPantalla = size.height

                val campoIzquierda = margenCampo
                val campoArriba = margenCampo
                val campoDerecha = anchoPantalla - margenCampo
                val campoAbajo = altoPantalla - margenCampo

                val anchoCampo = campoDerecha - campoIzquierda
                val altoCampo = campoAbajo - campoArriba

                val anchoPorteria = anchoCampo * anchoPorteriaFactor
                val altoPorteria = altoCampo * altoPorteriaFactor
                val posXPorteria = campoIzquierda + (anchoCampo - anchoPorteria) / 2

                val alturaFranja = altoCampo / 8
                for (i in 0..7) {
                    drawRect(
                        color = if (i % 2 == 0) Color(0xFF4CAF50) else Color(0xFF388E3C),
                        topLeft = Offset(campoIzquierda, campoArriba + i * alturaFranja),
                        size = Size(anchoCampo, alturaFranja)
                    )
                }

                drawRect(
                    color = Color.White,
                    topLeft = Offset(campoIzquierda, campoArriba),
                    size = Size(anchoCampo, altoCampo),
                    style = Stroke(width = grosorBorde)
                )

                drawLine(
                    Color.White,
                    Offset(campoIzquierda + anchoCampo / 2, campoArriba),
                    Offset(campoIzquierda + anchoCampo / 2, campoAbajo),
                    strokeWidth = 5f
                )

                drawCircle(
                    color = Color.White,
                    radius = anchoCampo * 0.15f,
                    center = Offset(campoIzquierda + anchoCampo / 2, campoArriba + altoCampo / 2),
                    style = Stroke(width = 5f)
                )

                drawRect(
                    color = Color.White,
                    topLeft = Offset(posXPorteria, campoArriba),
                    size = Size(anchoPorteria, altoPorteria),
                    style = Stroke(width = 5f)
                )

                drawRect(
                    color = Color.White,
                    topLeft = Offset(posXPorteria, campoAbajo - altoPorteria),
                    size = Size(anchoPorteria, altoPorteria),
                    style = Stroke(width = 5f)
                )

                drawCircle(
                    color = Color.White,
                    radius = radioPelota,
                    center = Offset(posXPelota, posYPelota)
                )
            }
        }
    }
}
