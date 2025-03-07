package com.example.respuestautomtica

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log

class CallService : Service() {

    companion object {
        const val ID_CANAL = "CanalServicioLlamadas"
    }

    override fun onStartCommand(intento: Intent?, flags: Int, idInicio: Int): Int {
        crearCanalNotificaciones()

        val notificacion = NotificationCompat.Builder(this, ID_CANAL)
            .setContentTitle("Servicio de Llamadas")
            .setContentText("Monitorizando llamadas entrantes...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        startForeground(1, notificacion)

        Log.d("CallService", "Servicio iniciado en primer plano")
        return START_STICKY
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                ID_CANAL,
                "Canal de Servicio de Llamadas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para el servicio que gestiona llamadas"
            }
            (getSystemService(NotificationManager::class.java)?.createNotificationChannel(canal))
        }
    }

    override fun onBind(intento: Intent?) = null
}