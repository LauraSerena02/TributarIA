package com.example.tributaria.features.geminichatbot.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path



object ChatData {
    private const val GEMINI_API_KEY = "AIzaSyBM2Spv7Z2msj79c_s5ku1fBWxejtVA4NU" // Tu API key de Gemini
    private const val EXCHANGE_API_KEY = "375fe3d616d402e02f923cc8" // API key para tasas de cambio

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = GEMINI_API_KEY
    )

    private val chatHistory = mutableListOf<Content>()


    interface ExchangeRateApi {
        @GET("v6/{exchangeKey}/latest/USD")
        suspend fun getLatestRates(@Path("exchangeKey") exchangeKey: String): ExchangeRateResponse
    }

    data class ExchangeRateResponse(
        val result: String,
        val conversion_rates: Map<String, Double>?
    )

    private val exchangeApi = Retrofit.Builder()
        .baseUrl("https://v6.exchangerate-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ExchangeRateApi::class.java)

    suspend fun getResponse(prompt: String): Chat {
        return try {
            if (isCurrencyPrompt(prompt)) {
                return handleCurrencyPrompt(prompt)
            }

            val userMessage = content {
                text(prompt)
                role = "user"
            }
            chatHistory.add(userMessage)

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(*chatHistory.toTypedArray())
            }

            response.text?.let { responseText ->
                val modelMessage = content {
                    text(responseText)
                    role = "model"
                }
                chatHistory.add(modelMessage)
            }

            Chat(
                prompt = response.text ?: "No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            Chat(
                prompt = "Error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        return try {
            val userMessage = content {
                image(bitmap)
                text(prompt)
                role = "user"
            }
            chatHistory.add(userMessage)

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(*chatHistory.toTypedArray())
            }

            response.text?.let { responseText ->
                val modelMessage = content {
                    text(responseText)
                    role = "model"
                }
                chatHistory.add(modelMessage)
            }

            Chat(
                prompt = response.text ?: "No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            Chat(
                prompt = "Error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    fun clearHistory() {
        chatHistory.clear()
    }

    private fun isCurrencyPrompt(prompt: String): Boolean {
        val keywords = listOf("dólar", "euro", "peso", "cotización", "divisa", "tipo de cambio", "moneda", "USD", "COP", "EUR", "MXN", "BTC", "GBP", "tasa de cambio")
        return keywords.any { prompt.contains(it, ignoreCase = true) }
    }

    private suspend fun handleCurrencyPrompt(prompt: String): Chat {
        return try {
            val apiResponse = withContext(Dispatchers.IO) {
                exchangeApi.getLatestRates(EXCHANGE_API_KEY)
            }

            val rates = apiResponse.conversion_rates ?: return Chat("No se pudo obtener la tasa de cambio", null, false)

            val selectedRates = rates.filterKeys { it in listOf("EUR", "MXN", "COP", "BTC", "GBP") }

            val ratesText = selectedRates.entries.joinToString("\n") {
                "• 1 USD = ${it.value} ${it.key}"
            }

            val fullPrompt = """
                El usuario preguntó: "$prompt"
                Aquí están los datos actuales de cambio desde 1 USD:
                $ratesText
            """.trimIndent()

            val userMessage = content {
                text(fullPrompt)
                role = "user"
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(userMessage)
            }

            response.text?.let { responseText ->
                val modelMessage = content {
                    text(responseText)
                    role = "model"
                }
                chatHistory.add(modelMessage)
            }

            Chat(response.text ?: "No response", null, false)
        } catch (e: Exception) {
            Chat("Error al consultar tasas: ${e.message}", null, false)
        }
    }
}

