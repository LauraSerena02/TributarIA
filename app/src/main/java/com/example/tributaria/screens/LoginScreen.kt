// Import required libraries for UI elements, state management, navigation, and resources
package com.example.tributaria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tributaria.R
import androidx.compose.ui.text.style.TextDecoration

// User data model containing username and password
data class User(val username: String, val password: String)

@Composable
fun LoginScreen(navController: NavHostController) {
    // Hardcoded user list for login validation
    val users = listOf(
        User("admin", "1234"),
        User("user1", "password"),
        User("guest", "guest123")
    )

    // State variables for input fields and password visibility
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State variables to manage input validation errors
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Main vertical layout with gradient background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFF90ACDD),
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp)) // Top spacing

        // App logo image
        Image(
            painter = painterResource(id = R.drawable.logotria),
            contentDescription = "Login Image",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        // Title text
        Text(
            text = "Iniciar sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.Black
        )

        // Username input field with validation
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (it.isNotBlank()) usernameError = false
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "User Icon") },
            placeholder = { Text("nombre@dominio.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            isError = usernameError,
            supportingText = {
                if (usernameError) Text("Required field", color = Color.Red)
            }
        )

        // Password input field with visibility toggle and validation
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon") },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = passwordError,
            supportingText = {
                if (passwordError) Text("Required field", color = Color.Red)
            }
        )

        // Login button with validation and navigation logic
        Button(
            onClick = {
                // Validate empty fields
                usernameError = username.isBlank()
                passwordError = password.isBlank()

                // If both fields are valid, check credentials
                if (!usernameError && !passwordError) {
                    if (users.any { it.username == username && it.password == password }) {
                        // Navigate to Success screen without stacking duplicates
                        navController.navigate("success") {
                            launchSingleTop = true
                        }
                    } else {
                        // Navigate to Failure screen
                        navController.navigate("failure") {
                            launchSingleTop = true
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2536A7))
        ) {
            Text("Login", color = Color.White)
        }

        // Button to create a new account (action not implemented)
        Button(
            onClick = {
                // Action for creating a new account (to be implemented)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2536A7))
        ) {
            Text("Create Account", color = Color.White)
        }

        // "Forgot Password" text with click action
        Text(
            text = "Forgot your password?",
            color = Color.DarkGray,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { /* Action for forgot password */ }
        )

        // Important legal disclaimer
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "**Important Notice**: TributarIA is an independent application and " +
                    "is not affiliated with or representing any government entity. The information " +
                    "provided is based on official public sources.",
            fontSize = 10.sp,  // Small text size
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(),
            color = Color.DarkGray
        )
    }
}
