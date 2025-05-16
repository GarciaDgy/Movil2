package com.example.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: "No title"
        val body = remoteMessage.data["body"] ?: "No content"

        Log.d("FCM", "Mensaje recibido: $title - $body")

        mostrarNotificacion(title, body)

        enviarMensajeAUI(title, body)
    }

    private fun mostrarNotificacion(title: String, body: String) {
        val channelId = "default"
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                description = "Canal para notificaciones importantes"
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }

    private fun enviarMensajeAUI(title: String, body: String) {
        val intent = Intent("com.example.app.NOTIFICATION")
        intent.putExtra("title", title)
        intent.putExtra("body", body)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
