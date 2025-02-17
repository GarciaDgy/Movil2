package com.example.divisaapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.divisaapp.network.RetrofitClient
import com.example.divisaapp.ui.theme.DivisaAppTheme
import com.example.divisaapp.worker.ExchangeRateWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scheduleExchangeRateUpdate()
        setContent {
            DivisaAppTheme {
                MainScreen()
            }
        }
    }

    private fun scheduleExchangeRateUpdate() {
        val workRequest = PeriodicWorkRequestBuilder<ExchangeRateWorker>(1, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "exchange_rate_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun MainScreen() {
    var exchangeRates by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var lastUpdatedTime by remember { mutableStateOf("Cargando...") }
    var nextUpdateTime by remember { mutableStateOf("Cargando...") }

    LaunchedEffect(Unit) {
        val (rates, lastUpdate) = fetchExchangeRates() ?: (emptyList<Pair<String, Double>>() to "Error")
        exchangeRates = rates
        lastUpdatedTime = lastUpdate

        // Calcular la fecha de la próxima actualización (1 hora después de la última)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lastUpdateDate = sdf.parse(lastUpdate)
        val nextUpdateDate = lastUpdateDate?.let { Date(it.time + TimeUnit.HOURS.toMillis(1)) }
        nextUpdateTime = nextUpdateDate?.let { sdf.format(it) } ?: "Desconocida"
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar la fecha de última actualización y próxima actualización
            Text(text = "Última actualización: $lastUpdatedTime", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Próxima actualización: $nextUpdateTime", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la lista de divisas con Scroll
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exchangeRates) { (currency, rate) ->
                    Text(text = "$currency: $rate", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Función que obtiene las tasas de cambio y la fecha actual
suspend fun fetchExchangeRates(): Pair<List<Pair<String, Double>>, String>? {
    return try {
        val response = RetrofitClient.apiService.getExchangeRates("18e39cdfa7f32ec8ec05203b")

        // Convertimos la respuesta en una lista de pares (moneda, tasa)
        val ratesList = response.conversion_rates.map { (currency, rate) -> currency to rate }

        // Obtener la fecha y hora actual formateada
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = sdf.format(Date())

        Log.d("API_SUCCESS", "Datos recibidos: $ratesList \nÚltima actualización: $currentTime")
        ratesList to currentTime
    } catch (e: Exception) {
        Log.e("API_FAILURE", "Fallo en la solicitud: ${e.message}")
        null
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DivisaAppTheme {
        MainScreen()
    }
}