package com.example.divisaapp.network


data class ExchangeRateResponse(
    val conversion_rates: Map<String, Double>
)