package com.example.tributaria.features.foro.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.features.foro.model.postViewModel
import com.example.tributaria.features.foro.presentation.components.MessageCard
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import com.example.tributaria.headers.HeaderForo

@Composable
fun ForoScreen(
    navController: NavHostController,
    viewModel: postViewModel = viewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val posts = viewModel.posts.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentUserId = loginViewModel.currentUserId
    var searchQuery by remember { mutableStateOf("") }

    val filteredPosts = posts.filter {
        it.userName.contains(searchQuery, ignoreCase = true) ||
                it.Title.contains(searchQuery, ignoreCase = true) ||
                it.body.contains(searchQuery, ignoreCase = true)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadPosts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                navController = navController,
                onLogout = { /* Logout logic */ }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar {
                    HeaderForo(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("add_post") }) {
                    Icon(Icons.Default.AddBox, contentDescription = "Compose")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            color = Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Buscar por autor, título o contenido",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Search",
                                        tint = Color.Gray
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            cursorColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredPosts.isEmpty()) {
                    Text(
                        text = "No se encontraron publicaciones",
                        color = Color.Blue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredPosts) { post ->
                            MessageCard(
                                post = post,
                                currentUserId = currentUserId.toString(),
                                navController = navController,
                                onDelete = { postId: String -> viewModel.deletePost(postId) },
                                highlightQuery = searchQuery
                            )
                        }
                    }
                }
            }
        }
    }
}