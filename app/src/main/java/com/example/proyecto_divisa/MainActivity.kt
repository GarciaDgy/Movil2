@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.proyecto_divisa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto_divisa.ui.theme.Proyecto_DivisaTheme
import com.example.proyecto_divisa.viewmodel.ExchangeRateViewModel
import com.example.proyecto_divisa.worker.scheduleExchangeRateWork

class MainActivity : ComponentActivity() {
    private val viewModel: ExchangeRateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleExchangeRateWork(applicationContext)

        setContent {
            Proyecto_DivisaTheme {
                val exchangeRates by viewModel.exchangeRates.collectAsState()
                val lastUpdate by viewModel.lastUpdate.collectAsState()
                val nextUpdate by viewModel.nextUpdate.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ExchangeRateScreen(
                            exchangeRates = exchangeRates,
                            lastUpdate = lastUpdate,
                            nextUpdate = nextUpdate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateScreen(exchangeRates: String?, lastUpdate: String?, nextUpdate: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App de Divisas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Última actualización: ${lastUpdate ?: "Cargando..."}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                Text(text = "Próxima actualización: ${nextUpdate ?: "Cargando..."}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (exchangeRates.isNullOrEmpty()) {
                    Text(
                        text = "No se encontraron tasas de cambio. Revisa tu conexión o la base de datos.",
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = "Tasas de cambio actuales:", fontWeight = FontWeight.Bold, color = Color(0xFFBF360C))
                    Column {
                        exchangeRates.split("\n").forEach { rate ->
                            Text(text = "$rate", color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
