package com.example.tributaria.features.foro.presentation.components

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.repository.Post

@Composable
fun PostOptionsMenu(
    post: Post?,
    navController: NavHostController,
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menú opciones")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Eliminar", color = Color.Red) },
                onClick = {
                    onDelete(post!!.id)
                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                    expanded = false
                    }

                )
                DropdownMenuItem(
                    text = { Text("Editar", color = Color.Blue) },
                    onClick = {
                        navController.navigate("add_post/${post!!.id}")
                        expanded = false
                    }
                )

        }
    }
}