package app.titova.vkr.db


data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ispolnitel: String = "",
    val header: String = "",
    val status: String = "",
    val time_start: String = "",
    val time_end: String = ""
)
