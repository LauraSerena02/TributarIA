package com.example.tributaria.features.foro.presentation.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedText(
    fullText: String,
    query: String,
    modifier: Modifier = Modifier,
    highlightColor: Color = Color.Blue,
    fontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val annotatedString = remember(fullText, query) {
        buildAnnotatedString {
            val startIndex = fullText.indexOf(query, ignoreCase = true)
            if (startIndex >= 0) {
                withStyle(style = SpanStyle(fontWeight = fontWeight)) {
                    append(fullText.substring(0, startIndex))
                }
                withStyle(style = SpanStyle(color = highlightColor, fontWeight = fontWeight)) {
                    append(fullText.substring(startIndex, startIndex + query.length))
                }
                withStyle(style = SpanStyle(fontWeight = fontWeight)) {
                    append(fullText.substring(startIndex + query.length))
                }
            } else {
                withStyle(style = SpanStyle(fontWeight = fontWeight)) {
                    append(fullText)
                }
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}