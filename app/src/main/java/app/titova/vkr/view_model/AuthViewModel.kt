import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus

    fun loginUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginStatus.value = "Invalid email format."
            return
        }
        if (password.length < 6) {
            _loginStatus.value = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginStatus.value = "Login Successful."
                    } else {
                        _loginStatus.value = "Authentication Failed: ${task.exception?.message}"
                    }
                }
        }
    }

    fun registerUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginStatus.value = "Invalid email format."
            return
        }
        if (password.length < 6) {
            _loginStatus.value = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginStatus.value = "Registration Successful."
                    } else {
                        _loginStatus.value = "Registration Failed: ${task.exception?.message}"
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
