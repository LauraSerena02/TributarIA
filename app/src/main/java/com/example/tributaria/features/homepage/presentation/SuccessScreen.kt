package com.example.tributaria.features.success.presentation

import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tributaria.*
import com.example.tributaria.features.success.viewmodel.SuccessViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.tributaria.headers.HeaderContentSuccess
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.tributaria.R
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.register.viewmodel.CreateAccountViewModel

@Composable
fun SuccessScreen(
    navController: NavHostController,
    viewModel: SuccessViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val newsList by viewModel.newsList.collectAsState()
    val refreshing = remember { mutableStateOf(false) }
    val username by loginViewModel.userName.collectAsState(initial = "")

    // llama checkUserSession al cargar la pantalla
    LaunchedEffect(Unit) {
        loginViewModel.checkUserSession()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                navController = navController,
                onLogout = {}
            )
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar {
                    HeaderContentSuccess(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        username = username
                    )
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = refreshing.value),
                onRefresh = {
                    refreshing.value = true
                    viewModel.getNews("tributaria")
                    refreshing.value = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    NewsSection(navController, newsList)
                    IndicatorsSection(viewModel)
                }
            }
        }
    }
}

@Composable
fun NewsSection(navController: NavController, newsList: List<News>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Noticias", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(2.dp)
                        .background(Color.Black)
                )
            }

            Button(
                onClick = {
                    navController.navigate("news") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AB))
            ) {
                Text("Ver noticias", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (newsList.isNotEmpty()) {
            NewsCarousel(newsList, navController)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Error, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(64.dp))
                Text("No se encontraron noticias sobre finanzas en este momento.", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun NewsCarousel(newsList: List<News>, navController: NavController) {
    val infinitePagesCount = Int.MAX_VALUE
    val startPage = infinitePagesCount / 2
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { infinitePagesCount })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000L)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 36.dp),
        pageSpacing = 6.dp
    ) { index ->
        val page = index % newsList.size
        val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
        val scale = if (pageOffset != 0f) {
            1f - (0.1f * kotlin.math.abs(pageOffset))
        } else {
            1f
        }

        Card(
            modifier = Modifier
                .height(260.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .padding(horizontal = 8.dp)
                .clickable {
                    navController.navigate("${Destinations.DETAILS_SCREEN}/${newsList[page].title}")
                },
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = newsList[page].urlToImage ?: R.drawable.placeholder,
                    contentDescription = "News Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = newsList[page].title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun IndicatorsSection(viewModel: SuccessViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Column {
            Text("Tasa de cambio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(2.dp)
                    .background(Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.trm_placeholer),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("TRM", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ShowChart, contentDescription = "Trending Up", tint = Color.Green)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )

                    when {
                        state.isLoading -> {
                            Text("Cargando...", color = Color.White)
                        }
                        state.errorMessage != null -> {
                            Text("Error: ${state.errorMessage}", color = Color.Red)
                        }
                        state.exchangeRate != null -> {
                            Text("USD", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("1 USD", color = Color.White, fontSize = 16.sp)
                            Text("COP", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("${state.exchangeRate} COP", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
