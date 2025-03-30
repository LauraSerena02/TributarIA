package com.example.tributaria

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun CommonTopBar(headerContent: @Composable () -> Unit) {
    // Container that takes the full width
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Displays the provided header content
        headerContent()
    }
}