package app.titova.vkr

import AuthViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.lifecycle.lifecycleScope
import app.titova.vkr.ui.theme.VkrTheme
import app.titova.vkr.view.app_activity.AuthScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // Если пользователь уже авторизован, получаем его данные
            authViewModel.fetchUserDetails(auth.currentUser?.email ?: "")
            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Наблюдаем за статусом входа и деталями пользователя
        lifecycleScope.launch {
            authViewModel.loginStatus.collect { status ->
                if (status == "Вход выполнен успешно.") {
                    // Переход на основной экран после успешного входа
                    val intent = Intent(this@AuthActivity, AppActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            authViewModel.userDetails.collect { details ->
                details?.let {
                    // Обрабатываем данные пользователя, если они есть, например, передаем их в другое Activity
                }
            }
        }

        setContent {
            VkrTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AuthScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}
