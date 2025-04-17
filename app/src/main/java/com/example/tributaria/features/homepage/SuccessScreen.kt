package com.example.tributaria.features.homepage

// Required imports for layout, components, and navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tributaria.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.ShowChart
import com.example.tributaria.CommonTopBar
import com.example.tributaria.BottomNavigationBar
import com.example.tributaria.CustomDrawer
import com.example.tributaria.headers.HeaderContentSuccess
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.navigation.NavController


@Composable
fun SuccessScreen(navController: NavHostController) {
    // Create a drawer state to manage the navigation drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Wrap the screen with a ModalNavigationDrawer to provide a side menu
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CustomDrawer(
                navController = navController,
                onLogout = { /* Agregar lógica de cierre de sesión */ }
            )
        }
    ) {
        // Use Scaffold to structure the screen layout with a top bar and bottom navigation
        Scaffold(
            topBar = {
                CommonTopBar {
                    // The top bar header content specific to this screen
                    HeaderContentSuccess(onMenuClick = { scope.launch { drawerState.open() } })
                }
            },
            bottomBar = { BottomNavigationBar(navController) } // Bottom navigation bar
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Display the news section and financial indicators section
                NewsSection(navController)
                IndicatorsSection()
            }
        }
    }
}

@Composable
fun NewsSection(navController: NavController) {
    // List of news headlines
    val newsList = listOf(
        "¿Es oportuna la terminación de la modalidad de importación?",
        "Nuevas regulaciones fiscales para 2025",
        "El impacto de la inflación en las importaciones",
        "Cómo optimizar los procesos de aduanas"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        // Header section with title and an underline
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

            // Button to navigate to the news section
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

        // Display a horizontal news carousel
        NewsCarousel(newsList)
    }
}

@Composable
fun NewsCarousel(newsList: List<String>) {
    val infinitePagesCount = Int.MAX_VALUE // A very large number to simulate an infinite carousel
    val startPage = infinitePagesCount / 2  // Start in the middle to allow scrolling in both directions

    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { infinitePagesCount })

    // Auto-scroll to the next page every 3 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000L)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    // Horizontal pager for displaying news cards
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 36.dp), // Allows side previews
        pageSpacing = 6.dp // Space between cards
    ) { index ->
        val page = index % newsList.size  // Use modulo to cycle through items
        val pageOffset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
        val scale = 1f - (0.1f * kotlin.math.abs(pageOffset)) // Reduce size on the sides

        // News card with image and title
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
fun IndicatorsSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Exchange rate section header with an underline
        Column {
            Text(
                text = "Tasa de cambio",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Box(
                modifier = Modifier
                    .width(130.dp) // Manually adjust if the text changes
                    .height(2.dp)
                    .background(Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card displaying the exchange rate information
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), // More rounded corners
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                // Background image for the exchange rate section
                Image(
                    painter = painterResource(id = R.drawable.trm_placeholer), // Use the correct image
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                // Semi-transparent overlay with exchange rate details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f)) // Darken the background
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Trending Up",
                            tint = Color.Green
                        )
                    }

                    // Divider line between sections
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.White
                    )

                    // Display exchange rates
                    Text(
                        text = "USD",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1 USD",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "COP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "4.112.0985 COP",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}