package com.example.tributaria.features.foro.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.repository.Post

@Composable
fun PostOptionsMenu(
    post: Post,
    currentUserId: String,
    navController: NavHostController,
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menú opciones")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Marcar como favorito") },
                onClick = {
                    // Acción marcar favorito
                    expanded = false
                }
            )

            if (post.authorId == currentUserId) {
                DropdownMenuItem(
                    text = { Text("Eliminar", color = Color.Red) },
                    onClick = {
                        onDelete(post.id)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Editar", color = Color.Blue) },
                    onClick = {
                        navController.navigate("add_post/${post.id}")
                        expanded = false
                    }
                )
            }
        }
    }
}