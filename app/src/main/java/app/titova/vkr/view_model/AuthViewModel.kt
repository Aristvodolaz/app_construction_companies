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

    // Добавляем StateFlow для данных пользователя
    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: StateFlow<UserDetails?> = _userDetails

    fun loginUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginStatus.value = "Неверный формат электронной почты."
            return
        }
        if (password.length < 6) {
            _loginStatus.value = "Пароль должен содержать не менее 6 символов."
            return
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginStatus.value = "Вход выполнен успешно."
                        fetchUserDetails(email)
                    } else {
                        _loginStatus.value = "Ошибка авторизации: ${task.exception?.message}"
                    }
                }
        }
    }

    fun fetchUserDetails(email: String) {
        // Заменяем "." на "_" для соответствия с ключом в базе данных
        val sanitizedEmail = email.replace(".", "_")
        val database = FirebaseDatabase.getInstance().getReference("Users/$sanitizedEmail")

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userDetails = snapshot.getValue(UserDetails::class.java)
                _userDetails.value = userDetails
            } else {
                _userDetails.value = UserDetails(error = "Информация о пользователе не найдена.")
            }
        }.addOnFailureListener { exception ->
            _userDetails.value = UserDetails(error = "Ошибка при получении информации о пользователе: ${exception.message}")
        }
    }

    fun registerUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginStatus.value = "Неверный формат электронной почты."
            return
        }
        if (password.length < 6) {
            _loginStatus.value = "Пароль должен содержать не менее 6 символов."
            return
        }

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginStatus.value = "Регистрация выполнена успешно."
                    } else {
                        _loginStatus.value = "Ошибка регистрации: ${task.exception?.message}"
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// Модель данных для пользователя
data class UserDetails(
    val dolzhnost: String? = null,
    val login: String? = null,
    val name: String? = null,
    val otdel: String? = null,
    val phone: String? = null,
    val surname: String? = null,
    val type: String? = null,
    val error: String? = null
)
