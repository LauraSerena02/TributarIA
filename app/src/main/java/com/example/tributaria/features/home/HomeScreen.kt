package com.example.tributaria.features.home

import android.net.Uri
import android.widget.VideoView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tributaria.R
import androidx.navigation.NavController

@Composable
fun VideoBackgroundView() {
    val context = LocalContext.current

    AndroidView(
        factory = {
            VideoView(it).apply {
                val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.background_video}")
                setVideoURI(videoUri)

                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    mediaPlayer.setVolume(0f, 0f) // Sin sonido

                    val videoWidth = mediaPlayer.videoWidth
                    val videoHeight = mediaPlayer.videoHeight
                    val screenRatio = context.resources.displayMetrics.run { heightPixels.toFloat() / widthPixels.toFloat() }
                    val videoRatio = videoHeight.toFloat() / videoWidth.toFloat()

                    if (videoRatio > screenRatio) {
                        scaleX = videoRatio / screenRatio
                    } else {
                        scaleY = screenRatio / videoRatio
                    }

                    start()
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Video de fondo
        VideoBackgroundView()

        // Capa semitransparente para legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 40.dp) // Más cerca del borde inferior
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Simplifica tu vida fiscal.\nGestiona tus obligaciones tributarias de forma fácil, rápida y segura.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3700B3)),
                    shape = MaterialTheme.shapes.medium,
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        "Comenzar ahora",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
