package com.example.tributaria.features.geminichatbot.viewmodel

import android.graphics.Bitmap
import com.example.tributaria.features.geminichatbot.data.Chat


data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)