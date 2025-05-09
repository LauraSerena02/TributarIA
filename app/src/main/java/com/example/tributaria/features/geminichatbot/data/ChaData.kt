package com.example.tributaria.features.geminichatbot.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content


object ChatData {
    private val api_key = "AIzaSyDfYP04ar3K2JPq_Ki0lVSlhBFNl2zmSCc"


    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = api_key
    )


    private val chatHistory = mutableListOf<Content>()

    /**
     * Obtiene una respuesta de texto del modelo Gemini
     * @param prompt Mensaje del usuario
     * @return Objeto Chat con la respuesta
     */
    suspend fun getResponse(prompt: String): Chat {
        try {

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

            return Chat(
                prompt = response.text ?: "No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            return Chat(
                prompt = "Error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    /**
     * Obtiene una respuesta multimodal (imagen + texto) del modelo Gemini
     * @param prompt Mensaje del usuario
     * @param bitmap Imagen adjunta
     * @return Objeto Chat con la respuesta
     */
    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        try {

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

            return Chat(
                prompt = response.text ?: "No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            return Chat(
                prompt = "Error: ${e.message ?: "Unknown error"}",
                bitmap = null,
                isFromUser = false
            )
        }
    }


    fun clearHistory() {
        chatHistory.clear()
    }
}