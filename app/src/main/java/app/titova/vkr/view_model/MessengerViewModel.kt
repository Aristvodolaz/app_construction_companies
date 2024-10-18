package app.titova.vkr.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.titova.vkr.db.Chat
import app.titova.vkr.db.ChatMessage
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessengerViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> get() = _chats

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> get() = _messages

    init {
        loadChats()
    }

    fun getMessagesForChat(chatId: String): StateFlow<List<ChatMessage>> {
        val formattedChatId = chatId.replace('.', '_')
        val chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
        val reference = FirebaseDatabase.getInstance().getReference("Chats/$formattedChatId/Messages")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = snapshot.children.mapNotNull { dataSnapshot ->
                    try {
                        dataSnapshot.getValue(ChatMessage::class.java)
                    } catch (e: DatabaseException) {
                        // Логируем ошибку и продолжаем
                        e.printStackTrace()
                        null
                    }
                }
                chatMessages.value = messagesList
                _messages.value = _messages.value.toMutableMap().apply {
                    put(formattedChatId, messagesList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })

        return chatMessages
    }


    fun sendMessage(messageText: String, sender: String, receiver: String, chatId: String) {
        if (messageText.isBlank()) return

        val message = ChatMessage(
            message = messageText,
            sender = sender,
            receiver = receiver,
            time = getCurrentTime()
        )
        val formattedChatId = chatId.replace('.', '_')

        FirebaseDatabase.getInstance().getReference("Chats/$formattedChatId/Messages")
            .push()
            .setValue(message)
    }

    private fun getCurrentTime(): String {
        val currentTime = java.text.SimpleDateFormat("dd.MM HH:mm", java.util.Locale.getDefault())
        return currentTime.format(java.util.Date())
    }

    private fun loadChats() {
        // Загрузка списка чатов из Firebase
        FirebaseDatabase.getInstance().getReference("Chats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                    _chats.value = chatList
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
