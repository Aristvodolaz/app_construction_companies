package app.titova.vkr.db


data class Chat(
    val login: String = "",
    val name: String = ""
)

data class ChatMessage(
    val message: String = "",
    val sender: String = "",
    val receiver: String = "",
    val time: String = ""
)