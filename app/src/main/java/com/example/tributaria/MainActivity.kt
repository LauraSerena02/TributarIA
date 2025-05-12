package com.example.tributaria


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tributaria.features.calculator.VanTirCalculatorScreen
import com.example.tributaria.features.login.presentation.LoginScreen
import com.example.tributaria.features.register.presentation.CreateScreen
import com.example.tributaria.features.success.presentation.SuccessScreen
import com.example.tributaria.features.recoveraccount.presentation.RecoverScreen
import com.example.tributaria.features.geminichatbot.presentation.ChatScreen
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tributaria.features.balancepoint.presentation.BalancePointScreen
import com.example.tributaria.features.calendar.presentation.CalendarScreen
import com.example.tributaria.features.calendar.presentation.RentDeclarationCheckScreen
import com.example.tributaria.features.foro.model.postViewModel
import com.example.tributaria.features.foro.presentation.AddPostScreen
import com.example.tributaria.features.foro.presentation.ForoScreen
import com.example.tributaria.features.foro.presentation.PostDetailScreen
import com.example.tributaria.features.home.HomeScreen
import com.example.tributaria.features.news.presentation.DetailsScreen
import com.example.tributaria.features.news.presentation.NewsScreen
import com.example.tributaria.features.moneylend.presentation.MoneyLendScreen



import dagger.hilt.android.AndroidEntryPoint



object Destinations {
    const val LIST_SCREEN = "LIST_SCREEN"
    const val DETAILS_SCREEN = "DETAILS_SCREEN"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val activity = LocalActivity.current

    BackHandler(enabled = true) {
        when (currentRoute) {
            "register", "recover" -> {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = false }
                }
            }
            "HomeScreen" -> {
                activity?.finish()
            }
            "login" -> {
                navController.navigate("HomeScreen") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> {
                navController.navigate("success") {
                    popUpTo("success") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = "HomeScreen",
        modifier = Modifier
    ) {
        composable("HomeScreen") { HomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("success") { SuccessScreen(navController) }
        composable("register") { CreateScreen(navController) }
        composable("recover") { RecoverScreen(navController) }
        composable("news") { NewsScreen(navController) }
        composable("van_tir") { VanTirCalculatorScreen(navController) }
        composable("balance_point") { BalancePointScreen(navController) }
        composable("gemini_ia") { ChatScreen(navController) }
        composable ("foro") {ForoScreen(navController)}
        composable ("moneylend") {MoneyLendScreen(navController)}
        composable ("calendar") { CalendarScreen(navController) }
        composable("rentDeclarationCheck") { RentDeclarationCheckScreen(navController) }
        composable("add_post") { AddPostScreen(navController = navController) }
        composable("postDetail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId == null) {
                // Si no hay postId, muestra un error o fallback
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: No se pudo cargar el post")
                }
            } else {
                PostDetailScreen(navController = navController, postId = postId)
            }
        }
        composable("add_post/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            val postViewModel: postViewModel = hiltViewModel()
            val post = postViewModel.getPostById(postId)
            if (postId != null && post == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando...")
                    }
                }
            } else {
                AddPostScreen(navController = navController, postToEdit = post)
            }
        }


        // ✅ Solo una entrada para NewsScreen
        composable(Destinations.LIST_SCREEN) {
            NewsScreen(navController)
        }

        // ✅ Detalles de noticia
        composable("${Destinations.DETAILS_SCREEN}/{newTitle}") {
            it.arguments?.getString("newTitle")?.let { title ->
                DetailsScreen(title, navController)
            }
        }
    }
}
