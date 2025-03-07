package com.example.respuestautomtica

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CallLog
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class CallReceiver : BroadcastReceiver() {
    companion object {
        private var ultimoNumero: String? = null
        private var mensajeEnviado = false
        private var estadoPrevio: String? = null

        var mensajePersonalizado: String = " "
    }

    override fun onReceive(contexto: Context?, intento: Intent?) {
        if (contexto == null || intento == null) {
            Log.e("CallReceiver", "Contexto o Intento son nulos, saliendo...")
            return
        }

        if (intento.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val estadoActual = intento.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (estadoActual == estadoPrevio) return
            estadoPrevio = estadoActual

            when (estadoActual) {
                TelephonyManager.EXTRA_STATE_RINGING -> manejarLlamadaEntrante(contexto, intento)
                TelephonyManager.EXTRA_STATE_IDLE -> manejarLlamadaTerminada()
                else -> Log.d("CallReceiver", "Estado de llamada no manejado: $estadoActual")
            }
        }
    }

    private fun manejarLlamadaEntrante(contexto: Context, intento: Intent) {
        var numeroEntrante = intento.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        if (numeroEntrante == null) {
            numeroEntrante = obtenerNumeroReciente(contexto)
        }

        if (numeroEntrante != null && !mensajeEnviado) {
            mensajeEnviado = true
            ultimoNumero = numeroEntrante
            Log.d("CallReceiver", "Detectado número: $numeroEntrante")
            enviarSmsAutomatico(contexto, numeroEntrante)
        }
    }

    private fun manejarLlamadaTerminada() {
        Log.d("CallReceiver", "Llamada terminada.")
        mensajeEnviado = false
    }

    private fun obtenerNumeroReciente(contexto: Context): String? {
        if (!tienePermisoLecturaLlamadas(contexto)) {
            Log.e("CallReceiver", "Permiso READ_CALL_LOG no concedido")
            return null
        }

        val cursor = contexto.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(CallLog.Calls.NUMBER),
            null, null,
            CallLog.Calls.DATE + " DESC"
        )

        return cursor?.use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) else null
        }
    }

    private fun tienePermisoLecturaLlamadas(contexto: Context): Boolean {
        return ContextCompat.checkSelfPermission(contexto, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
    }

    private fun enviarSmsAutomatico(contexto: Context, telefono: String) {
        val mensaje = mensajePersonalizado

        Log.d("CallReceiver", "Enviando SMS a: $telefono")
        Log.d("CallReceiver", "Mensaje: $mensaje")

        try {
            SmsManager.getDefault().sendTextMessage(telefono, null, mensaje, null, null)
            Log.d("CallReceiver", "SMS enviado correctamente a: $telefono")
        } catch (e: Exception) {
            Log.e("CallReceiver", "Error al enviar SMS: ${e.message}")
        }
    }
}