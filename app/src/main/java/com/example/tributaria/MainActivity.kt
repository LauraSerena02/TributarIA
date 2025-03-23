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
import com.example.tributaria.screens.LoginScreen
import com.example.tributaria.screens.SuccessScreen
import com.example.tributaria.screens.FailureScreen

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

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier
    ) {
        composable("login") { LoginScreen(navController) }
        composable("success") { SuccessScreen(navController) }
        composable("failure") { FailureScreen(navController) }
    }
}
