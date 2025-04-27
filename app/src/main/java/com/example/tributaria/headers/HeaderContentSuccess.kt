package com.example.tributaria.headers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tributaria.R

@Composable
fun HeaderContentSuccess(
    onMenuClick: () -> Unit,
    username: String // <-- nuevo parámetro
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF1E40AF))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 25.dp)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text("Hola, $username", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("¿Qué planificación realizaremos hoy?", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }
    }
}
