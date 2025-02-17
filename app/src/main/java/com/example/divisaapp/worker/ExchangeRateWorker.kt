package com.example.divisaapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.divisaapp.database.ExchangeRateDatabase
import com.example.divisaapp.network.RetrofitClient
import com.example.divisaapp.model.ExchangeRate
import retrofit2.HttpException

class ExchangeRateWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = ExchangeRateDatabase.getDatabase(applicationContext)
        val dao = db.exchangeRateDao()

        return try {
            // Hacemos la llamada a la API dentro de una coroutine
            val response = RetrofitClient.apiService.getExchangeRates("e752ff2208ffa575854247e8")

            val ratesList = response.conversion_rates.map { (currency, rate) ->
                ExchangeRate(currency = currency, rate = rate)
            }



            dao.deleteAllRates() // Eliminamos los datos previos
            dao.insertRates(ratesList) // Insertamos los nuevos datos


            Result.success()
        } catch (e: HttpException) {
            Result.retry() // Reintentar si hay error HTTP
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure() // Falla si ocurre otro error
        }
    }


}