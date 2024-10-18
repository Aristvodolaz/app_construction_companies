//package app.titova.vkr.view
//
//import AuthViewModel
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
//    var email by remember { mutableStateOf(TextFieldValue("")) }
//    var password by remember { mutableStateOf(TextFieldValue("")) }
//    var loginStatus by remember { mutableStateOf<String?>(null) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Brush.verticalGradient(
//                colors = listOf(Color(0xFF2196F3), Color(0xFF81D4FA))
//            )),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(24.dp)
//                .background(Color.White, shape = RoundedCornerShape(16.dp))
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Вход в систему",
//                style = MaterialTheme.typography.headlineMedium.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF1E88E5),
//                    fontSize = 28.sp
//                ),
//                modifier = Modifier.padding(bottom = 32.dp)
//            )
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedLabelColor = MaterialTheme.colorScheme.primary,
//                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                    containerColor = Color(0xFFF1F8E9)
//                )
//            )
//
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                visualTransformation = PasswordVisualTransformation(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 24.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedLabelColor = MaterialTheme.colorScheme.primary,
//                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                    containerColor = Color(0xFFF1F8E9)
//                )
//            )
//
//            Button(
//                onClick = {
//                    viewModel.loginUser(email.text, password.text)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF42A5F5)
//                )
//            ) {
//                Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            loginStatus?.let {
//                Text(
//                    text = it,
//                    color = if (it.contains("Successful")) Color(0xFF4CAF50) else Color(0xFFD32F2F),
//                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
//                    modifier = Modifier.padding(top = 16.dp)
//                )
//            }
//        }
//    }
//
//    LaunchedEffect(viewModel.loginStatus) {
//        if (viewModel.loginStatus == "Success") {
//            navController.navigate("home")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    val navController = rememberNavController()
//    val viewModel = AuthViewModel()
//    LoginScreen(navController = navController, viewModel = viewModel)
//}