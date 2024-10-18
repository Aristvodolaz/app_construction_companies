//package app.titova.vkr
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Message
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import app.titova.vkr.ui.theme.VkrTheme
//import app.titova.vkr.view.app_activity.AiScreen
//import app.titova.vkr.view.app_activity.TaskScreen
//import app.titova.vkr.view_model.TaskViewModel
//
//class MainActivity : ComponentActivity() {
//    private val taskViewModel: TaskViewModel by viewModels() // Use viewModels to get ViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            VkrTheme {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    val navController = rememberNavController()
//
//                    Scaffold(
//                        bottomBar = { BottomNavigationBar(navController) } // Добавляем нижнюю панель
//                    ) { innerPadding ->
//                        NavHost(
//                            navController = navController,
//                            startDestination = Screen.TaskBoard.route,
//                            modifier = Modifier.padding(innerPadding)
//                        ) {
//                            composable(Screen.TaskBoard.route) {
//                                TaskScreen(navController, taskViewModel)
//                            }
//                            composable(Screen.AI.route) {
//                                val modelPath = "helmet_detection.tflite"
//                                AiScreen(navController, modelPath)
//                            }
//                            // Здесь можно добавить дополнительные экраны
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Экран для навигации
//sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
//    object TaskBoard : Screen("task", "Tasks", Icons.Filled.Home)
//    object AI : Screen("ai", "AI", Icons.Filled.Person)
//    object Messenger : Screen("messenger", "Messenger", Icons.Filled.Message)
//}
//
//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    val items = listOf(
//        Screen.TaskBoard,
//        Screen.AI,
//        Screen.Messenger
//    )
//
//    // Следим за изменением состояния навигации
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    NavigationBar {
//        items.forEach { screen ->
//            NavigationBarItem(
//                icon = { Icon(screen.icon, contentDescription = null) },
//                label = { Text(screen.label) },
//                selected = currentRoute == screen.route,
//                onClick = {
//                    navController.navigate(screen.route) {
//                        popUpTo(navController.graph.startDestinationId)
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
//    }
//}
