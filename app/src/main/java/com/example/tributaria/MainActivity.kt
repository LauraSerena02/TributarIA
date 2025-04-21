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
import com.example.tributaria.features.news.NewsScreen
import com.example.tributaria.features.recoveraccount.presentation.RecoverScreen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState

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


    if (currentRoute != "success" && currentRoute != "login") {
        BackHandler {
            navController.navigate("success") {
                popUpTo("success") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier
    ) {
        composable("login") { LoginScreen(navController) }
        composable("success") { SuccessScreen(navController) }
        composable("register") { CreateScreen(navController) }
        composable("recover") { RecoverScreen(navController) }
        composable("news") { NewsScreen(navController) }
        composable("van_tir") { VanTirCalculatorScreen(navController) }
    }
}
