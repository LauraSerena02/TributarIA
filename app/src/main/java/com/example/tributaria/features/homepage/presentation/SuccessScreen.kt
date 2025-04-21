package com.example.tributaria.features.success.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.tributaria.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuccessScreen(navController: NavHostController, viewModel: SuccessViewModel = koinViewModel()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                    HeaderContentSuccess(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                NewsSection(navController)
                IndicatorsSection(viewModel)
            }
        }
    }
}

@Composable
fun NewsSection(navController: NavController) {
    val newsList = listOf(
        "¿Es oportuna la terminación de la modalidad de importación?",
        "Nuevas regulaciones fiscales para 2025",
        "El impacto de la inflación en las importaciones",
        "Cómo optimizar los procesos de aduanas"
    )

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
        NewsCarousel(newsList)
    }
}

@Composable
fun NewsCarousel(newsList: List<String>) {
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
        val scale = 1f - (0.1f * kotlin.math.abs(pageOffset))

        Card(
            modifier = Modifier
                .height(260.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.news_placeholder),
                    contentDescription = "News Image",
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = newsList[page],
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
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
