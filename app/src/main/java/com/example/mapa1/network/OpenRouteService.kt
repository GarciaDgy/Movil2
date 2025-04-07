package com.example.mapa1.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenRouteService {
    @POST("v2/directions/driving-car/geojson") // CAMBIADO de foot-walking a driving-car
    fun getRoute(
        @Query("api_key") apiKey: String,
        @Body body: RouteRequestBody
    ): Call<RouteResponse>
}
