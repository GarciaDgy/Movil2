@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.graficodivisa


import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.graficodivisa.ui.theme.GraficoDivisaTheme
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraficoDivisaTheme {
                ExchangeRateScreen()
            }
        }
    }
}

@Composable
fun ExchangeRateScreen() {
    val context = LocalContext.current
    val currencies = listOf("USD", "AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT","BGN","EUR", "GBP", "JPY", "CHF")
    var selectedCurrency by remember { mutableStateOf(currencies[0]) }
    var startDate by remember { mutableStateOf(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)) } // Hace 7 días
    var endDate by remember { mutableStateOf(System.currentTimeMillis()) } // Hoy
    var chartData by remember { mutableStateOf<List<Entry>?>(null) }




    //Se actualiza el grafico cuando cambia la divisa o el rengo de fechas
    LaunchedEffect(selectedCurrency, startDate, endDate) {
        chartData = obtenerDatosDesdeProvider(context.contentResolver, selectedCurrency, startDate, endDate)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            //Menu desplegable para seleccionar la divisa
            CurrencyDropdown(currencies, selectedCurrency) { newCurrency ->
                selectedCurrency = newCurrency
            }
            //Selección de rango de fechas
            DateSelectors { newStartDate, newEndDate ->
                startDate = newStartDate
                endDate = newEndDate
            }
//Mostrar grafico si hay datos disponibles
            if (!chartData.isNullOrEmpty()) {
                ShowExchangeRateChart(chartData!!)
            } else {
                Text("Cargando datos...", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

fun obtenerDatosDesdeProvider(
    contentResolver: ContentResolver,
    currency: String,
    startDate: Long?,
    endDate: Long?
): List<Entry>? {
    val contentUri = Uri.parse("content://com.example.proyecto_divisa.provider/exchange_rate")

    val selectionArgs = if (startDate != null && endDate != null) {
        arrayOf(startDate.toString(), endDate.toString())
    } else {
        null
    }

    val cursor: Cursor? = contentResolver.query(contentUri, null, null, selectionArgs, "lastUpdate ASC")

    val entries = mutableListOf<Entry>()
    val uniqueTimestamps = mutableSetOf<Long>()

    cursor?.use {
        val conversionRatesJsonIndex = it.getColumnIndex("conversionRatesJson")
        val lastUpdateIndex = it.getColumnIndex("lastUpdate")

        if (conversionRatesJsonIndex >= 0 && lastUpdateIndex >= 0) {
            while (it.moveToNext()) {
                val conversionRatesJson = it.getString(conversionRatesJsonIndex)
                val lastUpdate = it.getLong(lastUpdateIndex)

                // Tomar solo un dato por hora
                val hourTimestamp = lastUpdate / (60 * 60 * 1000) * (60 * 60 * 1000)
                if (!uniqueTimestamps.contains(hourTimestamp)) {
                    uniqueTimestamps.add(hourTimestamp)
                    entries.addAll(parseExchangeRatesToEntries(conversionRatesJson, currency, hourTimestamp))
                }
            }
        }
    }

    return if (entries.isNotEmpty()) entries else null
}


@Composable
fun CurrencyDropdown(currencies: List<String>, selectedCurrency: String, onCurrencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            readOnly = true,
            value = selectedCurrency,
            onValueChange = { },
            label = { Text("Selecciona una divisa") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateSelectors(onDateRangeSelected: (Long, Long) -> Unit) {
    val context = LocalContext.current
    var beforeDateTime by remember { mutableStateOf("") }
    var afterDateTime by remember { mutableStateOf("") }

    fun showDateTimePicker(onDateTimeSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val timePickerDialog = android.app.TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
                        }
                        onDateTimeSelected(selectedCalendar.timeInMillis)
                    },
                    hour,
                    minute,
                    true
                )
                timePickerDialog.show()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = {
                showDateTimePicker { selectedDateTime ->
                    beforeDateTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateTime))
                    val startDate = selectedDateTime
                    val endDate = parseDateTimeToMillis(afterDateTime)
                    if (startDate > 0 && endDate > 0) {
                        onDateRangeSelected(startDate, endDate)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Desde: ${beforeDateTime.ifEmpty { "Seleccionar" }}")
        }

        OutlinedButton(
            onClick = {
                showDateTimePicker { selectedDateTime ->
                    afterDateTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateTime))
                    val startDate = parseDateTimeToMillis(beforeDateTime)
                    val endDate = selectedDateTime
                    if (startDate > 0 && endDate > 0) {
                        onDateRangeSelected(startDate, endDate)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Hasta: ${afterDateTime.ifEmpty { "Seleccionar" }}")
        }
    }
}

fun parseDateTimeToMillis(dateTime: String): Long {
    return try {
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val date = format.parse(dateTime)
        date?.time ?: -1
    } catch (e: Exception) {
        -1
    }
}


@Composable
fun ShowExchangeRateChart(chartData: List<Entry>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val dataSet = LineDataSet(chartData, "Tasa de cambio").apply {
                    color = 0xFFE91E63.toInt() // Color rosa fuerte
                    valueTextColor = 0xFF3F51B5.toInt() // Azul oscuro
                    valueTextSize = 10f
                    setDrawCircles(true)
                    setCircleColor(0xFF673AB7.toInt()) // Morado oscuro
                    setDrawValues(true)
                }
                this.data = LineData(dataSet)
                this.invalidate()
                this.description.isEnabled = false
                this.setTouchEnabled(true)
                this.setPinchZoom(true)

                this.xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val date = Date(value.toLong())
                        val format = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())
                        return format.format(date)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(300.dp),
        update = { chart ->
            chart.clear()
            val dataSet = LineDataSet(chartData, "Tasa de cambio").apply {
                color = 0xFFE91E63.toInt() // Color rosa fuerte
                valueTextColor = 0xFF000080.toInt() // Azul oscuro
                valueTextSize = 10f
                setDrawCircles(true)
                setCircleColor(0xFF000000.toInt()) // Negro
                setDrawValues(true)
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}


fun parseExchangeRatesToEntries(json: String, selectedCurrency: String, timestamp: Long): List<Entry> {
    val gson = Gson()
    val type = object : TypeToken<Map<String, Float>>() {}.type
    val ratesMap: Map<String, Float> = gson.fromJson(json, type)

    val entries = mutableListOf<Entry>()
    ratesMap[selectedCurrency]?.let { rate ->
        entries.add(Entry(timestamp.toFloat(), rate)) // Se usa timestamp como eje X
    }
    return entries
}


