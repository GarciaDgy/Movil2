package com.example.respuestautomtica

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, CallService::class.java)
        startService(serviceIntent)

        setContent {
            AutoReplyCallAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var customMessage by remember {
        mutableStateOf(TextFieldValue(""))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Respuesta automática",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    color = Color(0xFF2193b0),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Número de teléfono") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { customMessage = it },
                    label = { Text("Mensaje de respuesta automática") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )

                Button(
                    onClick = {
                        val number = phoneNumber.text.trim()
                        val message = customMessage.text.trim()

                        if (number.isNotEmpty() && message.isNotEmpty()) {
                            CallReceiver.mensajePersonalizado = message
                            Toast.makeText(
                                context,
                                "Respuesta automática configurada para $number",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor ingrese un número y mensaje",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2193b0))
                ) {
                    Text("Activar Servicio", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AutoReplyCallAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2193b0),
            secondary = Color(0xFF6dd5ed),
            background = Color(0xFFf8f9fa),
            surface = Color.White
        ),
        content = content
    )
}
