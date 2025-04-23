package com.example.tributaria.features.news.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tributaria.Destinations
import com.example.tributaria.R
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.news.viewmodel.ListScreenViewModel
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CommonTopBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.headers.HeaderContentNews
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    navController: NavController,
    keyword: String,
    viewModel: ListScreenViewModel = hiltViewModel()
) {
    val newsList by viewModel.news.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Efecto que actualiza las noticias cuando cambia el keyword
    LaunchedEffect(keyword) {
        viewModel.fetchNewsByKeyword(keyword)
    }

    ListContent(navController, newsList, isLoading, errorMessage)
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    navController: NavController,
    news: List<News>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Scaffold(
        topBar = {
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(news) { new ->
                        Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("${Destinations.DETAILS_SCREEN}/${new.title}")
                                    },
                            ) {
                                Column {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f),
                                        painter = rememberImagePainter(
                                            data = new.urlToImage,
                                            builder = {
                                                placeholder(R.drawable.placeholder)
                                                error(R.drawable.placeholder)
                                            }
                                        ),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth
                                    )
                                    Column(Modifier.padding(8.dp)) {
                                        Text(new.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Text(new.content ?: "", maxLines = 3)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val categories = listOf("tributaria", "finanzas", "civil", "economia", "globales")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var searchQuery by remember { mutableStateOf("") }

    // Efecto que escucha los cambios del query para hacer la búsqueda automáticamente
    LaunchedEffect(searchQuery) {
        kotlinx.coroutines.delay(500)
        val query = searchQuery.trim()
        if (query.isNotEmpty()) {
            selectedCategory = query // Esto hará que ListScreen se actualice
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
                    HeaderContentNews(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar noticias") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp), // cambia vertical = 4.dp a solo bottom
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                searchQuery = ""
                            },
                            label = { Text(category) }
                        )
                    }
                }

                // Título debajo de las categorías, con menos padding
                Text(
                    text = "${selectedCategory.replaceFirstChar { it.uppercase() }} Noticias",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp) // puedes ajustar más si es necesario
                )

                ListScreen(
                    navController = navController,
                    keyword = selectedCategory
                )
            }

        }
    }
}
