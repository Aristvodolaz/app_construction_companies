package app.titova.vkr.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    var userLoggedIn: Boolean
        get() = sharedPreferences.getBoolean("userLoggedIn", false)
        set(value) = sharedPreferences.edit().putBoolean("userLoggedIn", value).apply()

    var userEmail: String?
        get() = sharedPreferences.getString("userEmail", null)
        set(value) = sharedPreferences.edit().putString("userEmail", value).apply()

    // Другие поля по необходимости
}
