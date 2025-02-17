package com.example.divisaapp.network

import com.example.divisaapp.network.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Url
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("{apiKey}/latest/USD")
    suspend fun getExchangeRates(@Path("apiKey") apiKey: String): ExchangeRateResponse
}