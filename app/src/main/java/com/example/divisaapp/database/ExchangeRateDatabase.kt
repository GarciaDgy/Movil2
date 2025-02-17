package com.example.divisaapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divisaapp.model.ExchangeRate

@Database(entities = [ExchangeRate::class], version = 1)
abstract class ExchangeRateDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile
        private var INSTANCE: ExchangeRateDatabase? = null

        fun getDatabase(context: Context): ExchangeRateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExchangeRateDatabase::class.java,
                    "exchange_rate_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
