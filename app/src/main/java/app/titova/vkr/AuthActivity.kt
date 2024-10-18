package app.titova.vkr

import AuthViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import app.titova.vkr.ui.theme.VkrTheme
import app.titova.vkr.view.app_activity.AuthScreen
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
            finish()
            return
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