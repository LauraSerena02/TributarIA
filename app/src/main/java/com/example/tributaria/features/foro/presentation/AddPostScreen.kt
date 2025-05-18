package com.example.tributaria.features.foro.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.model.postViewModel
import com.example.tributaria.features.foro.repository.Post
import com.example.tributaria.features.login.viewmodel.LoginViewModel

@Composable
fun AddPostScreen(
    navController: NavHostController,
    viewModel: postViewModel = viewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    postToEdit: Post? = null
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val userId = loginViewModel.currentUserId
    val context = LocalContext.current
    val username by loginViewModel.userName.collectAsState(initial = "")
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        loginViewModel.checkUserSession()
    }
    LaunchedEffect(postToEdit) {
        postToEdit?.let {
            title = it.Title
            body = it.body
        }
    }

    val isEditMode = postToEdit != null

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E40AF))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Editar Post" else "Nuevo Post",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    value = title.toString(),
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = body.toString(),
                    onValueChange = { body = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF1E40AF),
                        unfocusedIndicatorColor = Color(0xFF1E40AF),
                        focusedLabelColor = Color(0xFF1E40AF),
                        cursorColor = Color(0xFF1E40AF)
                    )
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title?.isBlank() == true || body?.isBlank() == true) {
                            errorMessage = "Por favor, complete todos los campos."
                            return@Button
                        } else {
                            errorMessage = ""
                        }

                        if (isEditMode) {
                            postToEdit?.let {
                                viewModel.updatePost(it.id, title.toString(), body)
                                Toast.makeText(context, "Publicación editada", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            viewModel.createPost(title.toString(), body, userId.toString(), username)
                            Toast.makeText(context, "Publicación agregada", Toast.LENGTH_SHORT).show()
                        }
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E40AF),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isEditMode) "Actualizar" else "Publicar")
                }
            }

        }
    }
}