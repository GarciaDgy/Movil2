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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var ballX by remember { mutableStateOf(300f) }
    var ballY by remember { mutableStateOf(500f) }
    val ballRadius = 20f

    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    val goalWidthFactor = 0.2f
    val goalHeight = 30f

    var score by remember { mutableStateOf(0) }

    val obstacleSize = Size(100f, 20f) // Tamaño de cada obstáculo

    val obstacles = listOf(
        Offset(200f, 300f),
        Offset(500f, 450f),
        Offset(350f, 600f),
        Offset(600f, 750f),
        Offset(150f, 550f)
    )

    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }

    val listener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]

                    velocityX = -x * 5
                    velocityY = y * 5

                    var newBallX = ballX + velocityX
                    var newBallY = ballY + velocityY

                    // Verificar colisiones con los obstáculos
                    for (obstacle in obstacles) {
                        val obstacleX = obstacle.x
                        val obstacleY = obstacle.y

                        val collisionX = newBallX + ballRadius > obstacleX &&
                                newBallX - ballRadius < obstacleX + obstacleSize.width
                        val collisionY = newBallY + ballRadius > obstacleY &&
                                newBallY - ballRadius < obstacleY + obstacleSize.height

                        if (collisionX && collisionY) {
                            val overlapX = min(
                                (obstacleX + obstacleSize.width) - (newBallX - ballRadius),
                                (newBallX + ballRadius) - obstacleX
                            )
                            val overlapY = min(
                                (obstacleY + obstacleSize.height) - (newBallY - ballRadius),
                                (newBallY + ballRadius) - obstacleY
                            )

                            if (overlapX < overlapY) {
                                velocityX = -velocityX // Rebote horizontal
                            } else {
                                velocityY = -velocityY // Rebote vertical
                            }

                            newBallX = ballX + velocityX
                            newBallY = ballY + velocityY
                        }
                    }

                    // Aplicar límites de la cancha
                    newBallX = max(ballRadius, min(screenWidth - ballRadius, newBallX))
                    newBallY = max(ballRadius, min(screenHeight - ballRadius, newBallY))

                    ballX = newBallX
                    ballY = newBallY

                    // Verificar si la pelota entra en la portería
                    val goalX = (screenWidth - screenWidth * goalWidthFactor) / 2
                    if (ballY - ballRadius <= goalHeight && ballX in goalX..(goalX + screenWidth * goalWidthFactor)) {
                        score++
                        ballX = screenWidth / 2
                        ballY = screenHeight / 2
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(sensorManager) {
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Goles: $score", modifier = Modifier.padding(16.dp))

        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            drawRoundRect(
                color = Color.Green,
                size = Size(screenWidth, screenHeight),
                cornerRadius = CornerRadius(20f, 20f)
            )

            val goalX = (screenWidth - screenWidth * goalWidthFactor) / 2
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(goalX, 0f),
                size = Size(screenWidth * goalWidthFactor, goalHeight),
                cornerRadius = CornerRadius(10f, 10f)
            )

            for (obstacle in obstacles) {
                drawRoundRect(
                    color = Color.Red,
                    topLeft = obstacle,
                    size = obstacleSize,
                    cornerRadius = CornerRadius(5f, 5f)
                )
            }

            drawCircle(
                color = Color.White,
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }
    }
}
