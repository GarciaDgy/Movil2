package com.example.divisaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey val currency: String,  // Código de la moneda (ej: "USD", "EUR")
    val rate: Double  // Valor del tipo de cambio
)
