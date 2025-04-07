package com.example.mapa1.map

import android.location.Location
import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mapa1.R
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapa1.util.HomeLocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.core.content.res.ResourcesCompat



@Composable
fun MapScreen(
    currentLocation: Location?,
    route: List<GeoPoint>,
    onNewHomeLocation: (Double, Double) -> Unit,
    onTrazarRuta: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var homeMarker by remember { mutableStateOf<Marker?>(null) }
    var selectingHome by remember { mutableStateOf(false) }
    var pendingGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = {
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    mapView = this

                    currentLocation?.let { loc ->
                        val geo = GeoPoint(loc.latitude, loc.longitude)
                        controller.setCenter(geo)

                        val locationMarker = Marker(this).apply {
                            position = geo
                            title = "Ubicación actual"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ResourcesCompat.getDrawable(context.resources, android.R.drawable.presence_busy, null)
                            showInfoWindow()
                        }

                        overlays.add(locationMarker)
                        invalidate()


                    }

                    coroutineScope.launch {
                        val saved = withContext(Dispatchers.IO) {
                            HomeLocationManager.getHomeLocation(context)
                        }
                        saved?.let { (lat, lon) ->
                            val point = GeoPoint(lat, lon)
                            homeMarker = Marker(this@apply).apply {
                                position = point
                                title = "Casa seleccionada"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(homeMarker)
                            invalidate()
                        }
                    }

                    setOnTouchListener { _, event ->
                        if (selectingHome && event.action == MotionEvent.ACTION_UP) {
                            val iGeoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                            pendingGeoPoint = GeoPoint(iGeoPoint.latitude, iGeoPoint.longitude)
                            showConfirmationDialog = true
                            true
                        } else false
                    }
                }
            }
        )

        LaunchedEffect(route) {
            mapView?.let { map ->
                map.overlays.removeAll { it is Polyline && it.title == "Ruta" }
                if (route.isNotEmpty()) {
                    val polyline = Polyline().apply {
                        setPoints(route)
                        title = "Ruta"
                    }
                    map.overlays.add(polyline)
                }
                map.invalidate()
            }
        }

        if (showConfirmationDialog && pendingGeoPoint != null) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmationDialog = false
                        selectingHome = false
                        val newPoint = pendingGeoPoint!!
                        coroutineScope.launch {
                            HomeLocationManager.saveHomeLocation(context, newPoint.latitude, newPoint.longitude)
                            onNewHomeLocation(newPoint.latitude, newPoint.longitude)
                            homeMarker?.let { mapView?.overlays?.remove(it) }
                            homeMarker = Marker(mapView).apply {
                                position = newPoint
                                title = "Casa seleccionada"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView?.overlays?.add(homeMarker)
                            mapView?.invalidate()
                        }
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showConfirmationDialog = false
                        selectingHome = false
                    }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmar nueva dirección de casa") },
                text = { Text("¿Quieres establecer este punto como tu casa?") }
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { selectingHome = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Establecer casa (toca el mapa)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                currentLocation?.let { loc ->
                    mapView?.controller?.setCenter(GeoPoint(loc.latitude, loc.longitude))
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Centrar en mi ubicación")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onTrazarRuta, modifier = Modifier.fillMaxWidth()) {
                Text("Trazar Ruta")
            }
        }
    }
}
