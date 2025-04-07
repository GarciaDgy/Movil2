package com.example.mapa1

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.mapa1.map.MapScreen
import com.example.mapa1.network.OpenRouteService
import com.example.mapa1.network.RouteRequestBody
import com.example.mapa1.util.HomeLocationManager
import com.example.mapa1.util.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var locationService: LocationService
    private lateinit var openRouteService: OpenRouteService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = LocationService(applicationContext)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openRouteService = retrofit.create(OpenRouteService::class.java)

        setContent {
            var currentLocation by remember { mutableStateOf<Location?>(null) }
            var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }

            LaunchedEffect(Unit) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationService.getCurrentLocation { location ->
                        currentLocation = location
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            MaterialTheme {
                MapScreen(
                    currentLocation = currentLocation,
                    route = routePoints,
                    onNewHomeLocation = { lat, lon ->
                        lifecycleScope.launch {
                            HomeLocationManager.saveHomeLocation(applicationContext, lat, lon)
                        }
                    },
                    onTrazarRuta = {
                        locationService.getCurrentLocation { location ->
                            currentLocation = location
                            lifecycleScope.launch {
                                val home = HomeLocationManager.getHomeLocation(applicationContext)
                                if (location != null && home != null) {
                                    requestRoute(
                                        start = GeoPoint(location.latitude, location.longitude),
                                        end = GeoPoint(home.first, home.second)
                                    ) { routePoints = it }
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "No hay una casa establecida.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun requestRoute(start: GeoPoint, end: GeoPoint, onResult: (List<GeoPoint>) -> Unit) {
        val body = RouteRequestBody(
            coordinates = listOf(
                listOf(start.longitude, start.latitude),
                listOf(end.longitude, end.latitude)
            )
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val response = openRouteService.getRoute(
                apiKey = "5b3ce3597851110001cf624878dee23a05784600b4d6236ef55fe21f",
                body = body
            ).execute()

            if (response.isSuccessful) {
                val coords = response.body()?.features?.firstOrNull()?.geometry?.coordinates
                val points = coords?.map { GeoPoint(it[1], it[0]) } ?: emptyList()
                launch(Dispatchers.Main) { onResult(points) }
            } else {
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al obtener ruta: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) recreate()
        }
}
