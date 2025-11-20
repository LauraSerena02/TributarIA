package com.example.tributaria.features.register.presentation


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tributaria.features.register.viewmodel.CreateAccountViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tributaria.R

// Función composable principal que representa la pantalla de creación de cuenta
@Composable
fun CreateScreen(navController: NavHostController, viewModel: CreateAccountViewModel = hiltViewModel()) {
    // Se suscribe al estado del ViewModel usando StateFlow
    val state by viewModel.state.collectAsState()
    // Controla la visibilidad del diálogo de términos
    var showTermsDialog by remember { mutableStateOf(false) }

    // Configura el lanzador para el resultado de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }
    // Navegación automática al login si el registro fue exitoso
    if (state.success) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("create") { inclusive = true } // Elimina la pantalla del backstack
            }
            viewModel.resetSuccess() // Reinicia la bandera de éxito
        }
    }

    // Contenedor principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espacio superior inicial
        Spacer(modifier = Modifier.height(60.dp))

        // Título
        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2271B3)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtítulo
        Text(
            text = "Organiza tus impuestos y toma el control de tus finanzas. ¡Regístrate ahora!",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
        // Separador visual
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
            Text(
                text = "O",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Campo de nombre de usuario
        OutlinedTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de correo electrónico con validación
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            isError = !state.isEmailValid,
            modifier = Modifier.fillMaxWidth()
        )
        if (!state.isEmailValid) {
            Text("Correo no válido", color = Color.Red)
        }

        // Campo de contraseña
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto
            modifier = Modifier.fillMaxWidth()
        )

        // Confirmar contraseña
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error si las contraseñas no coinciden
        if (state.passwordMismatch) {
            Text("Las contraseñas no coinciden", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Switch y texto para aceptar términos y condiciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = state.termsAccepted,
                onCheckedChange = viewModel::onTermsAcceptedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2271B3),
                    checkedTrackColor = Color(0xFFB0C5F0),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )
            Text(
                text = "Acepto los ",
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "términos de privacidad.",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2271B3),
                modifier = Modifier.clickable { showTermsDialog = true }
            )
        }

        // Diálogo con los términos y condiciones
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = {
                    Text(
                        "Términos de Privacidad",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2271B3)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "TributarIA, en cumplimiento de lo señalado en la Ley 1581 de 2012 y el Decreto Reglamentario 1377 de 2013.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TributarIA implementará todas las acciones necesarias para garantizar la protección y el tratamiento adecuado de los datos personales.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se protegerán los derechos a la privacidad, la intimidad y el buen nombre.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estas políticas aplican tanto para la protección de los datos personales como de la información transaccional tratada actualmente y en el futuro.",
                            textAlign = TextAlign.Justify,
                            lineHeight = 20.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Aceptar", color = Color(0xFF2271B3))
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para enviar el formulario de registro
        Button(
            onClick = viewModel::register,
            enabled = viewModel.isFormValid() && !state.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isFormValid()) Color(0xFF2271B3) else Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (state.isLoading)
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else
                Text("Registrarme", color = Color.White)
        }

        // Mensaje de error en caso de fallo
        state.error?.let {
            Text(it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nuevo: Botón para registro con Google
        OutlinedButton(
            onClick = {
                viewModel.registerWithGoogle(googleSignInLauncher)
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
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver a la pantalla de login
        OutlinedButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}