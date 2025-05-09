// Importa las librerías necesarias para Composables, UI y la interacción con el ViewModel
package com.example.tributaria.features.login.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tributaria.R
import com.example.tributaria.features.login.repository.AuthRepository
import com.example.tributaria.features.login.viewmodel.LoginState
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore


// Define la pantalla de inicio de sesión. Recibe el navController para la navegación y el LoginViewModel.
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()  // <- Cambio aquí
) {


    val loginState by viewModel.loginState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Añade este estado para el launcher de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    LaunchedEffect(Unit) {
        viewModel.checkUserSession()
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                viewModel.updateUsername("")
                viewModel.updatePassword("")
                navController.navigate("success") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginState.InvalidCredentials -> {
                val message = (loginState as LoginState.InvalidCredentials).errorMessage
                snackbarHostState.showSnackbar(message)
            }
            is LoginState.Error -> {
                val message = (loginState as LoginState.Error).errorMessage
                snackbarHostState.showSnackbar(message)
            }

            else -> Unit
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFF90ACDD))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logotria),
                contentDescription = "Login Image",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            // Título
            Text(
                text = "Iniciar sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Black
            )

            when (loginState) {
                is LoginState.InvalidCredentials -> {
                    Text(
                        text = "Email o contraseña incorrectos.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                is LoginState.Error -> {
                    Text(
                        text = (loginState as LoginState.Error).errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                else -> Unit
            }

            // Campo Email
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = viewModel::updateUsername,
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "User Icon") },
                placeholder = { Text("nombre@dominio.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                isError = loginState is LoginState.EmptyFields && viewModel.username.isBlank(),
                supportingText = {
                    if (loginState is LoginState.EmptyFields && viewModel.username.isBlank()) {
                        Text("Campo obligatorio", color = Color.Red)
                    }
                }
            )

            // Campo Contraseña
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Contraseña") },
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
                isError = loginState is LoginState.EmptyFields && viewModel.password.isBlank(),
                supportingText = {
                    if (loginState is LoginState.EmptyFields && viewModel.password.isBlank()) {
                        Text("Campo obligatorio", color = Color.Red)
                    }
                }
            )

            // Botón Login
            Button(
                onClick = {
                    viewModel.login()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = loginState != LoginState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2536A7))
            ) {
                if (loginState == LoginState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically)
                    )
                } else {
                    Text("Iniciar sesión", color = Color.White)
                }
            }

            // Botón Login con Google
            OutlinedButton(
                onClick = {
                    viewModel.loginWithGoogle(googleSignInLauncher)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFDB4437) // Color de Google
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continuar con Google", color = Color.Black)
                }
            }
            // Botón Crear Cuenta
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2536A7))
            ) {
                Text("Crear cuenta", color = Color.White)
            }

            // Enlace Olvidaste la Contraseña
            Text(
                text = "¿Olvidaste la contraseña?",
                color = Color.DarkGray,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("recover") {
                            launchSingleTop = true
                        }
                    }
            )

            // Important legal disclaimer
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "**Aviso importante**: TributarIA es una aplicacion independiente y " +
                        "no esta afiliada ni representa a ninguna entidad gubernamental. La información " +
                        "proporcionada se basa en fuentes públicas oficiales.",
                fontSize = 10.sp,  // Small text size
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                color = Color.DarkGray
            )
        }
    }
}
