package com.example.tributaria.features.foro.presentation.utils

import com.google.firebase.Timestamp


fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(timestamp.toDate())
}