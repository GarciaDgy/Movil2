package com.example.divisaapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.divisaapp.model.ExchangeRate


@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    fun getAllRates(): List<ExchangeRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRates(rates: List<ExchangeRate>)

    @Query("DELETE FROM exchange_rates")
    fun deleteAllRates()
}