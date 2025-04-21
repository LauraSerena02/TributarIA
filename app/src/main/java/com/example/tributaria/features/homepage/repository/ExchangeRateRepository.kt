package com.example.tributaria.features.homepage.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class ExchangeRateRepository {
    suspend fun getUsdToCopRate(): Double {
        return withContext(Dispatchers.IO) {
            val response = URL("https://v6.exchangerate-api.com/v6/375fe3d616d402e02f923cc8/latest/USD")
                .readText()
            val json = JSONObject(response)
            json.getJSONObject("conversion_rates").getDouble("COP")
        }
    }
}