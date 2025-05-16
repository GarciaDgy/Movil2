package com.example.app

import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import com.example.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private var currentToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val messageState = mutableStateOf("Esperando mensaje...")
        val context = this

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                currentToken = token
                messageState.value = "FCM Token: $token"
                Log.d("FCM", "Token recibido: $token")
            } else {
                messageState.value = "Error al obtener token"
                Log.e("FCM", "Error al obtener token", task.exception)
            }
        }

        setContent {
            AppTheme {
                FirebasePushUI(messageState, context) { currentToken }
            }
        }
    }
}

@Composable
fun FirebasePushUI(messageState: MutableState<String>, context: Context, getToken: () -> String?) {
    val clipboardManager = LocalClipboardManager.current
    val toastContext = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val title = intent.getStringExtra("title") ?: "Sin título"
                val body = intent.getStringExtra("body") ?: "Sin contenido"
                messageState.value = "Título: $title\nMensaje: $body"
            }
        }

        val filter = IntentFilter("com.example.app.NOTIFICATION")
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)

        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Centro de Notificaciones",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = messageState.value,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            OutlinedButton(
                onClick = {
                    val token = getToken()
                    if (token != null) {
                        clipboardManager.setText(AnnotatedString(token))
                        Toast.makeText(toastContext, "Token copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(toastContext, "Token no disponible", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copiar Token")
            }
        }
    }
}
