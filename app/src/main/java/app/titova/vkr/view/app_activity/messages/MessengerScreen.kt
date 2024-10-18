package app.titova.vkr.view.app_activity.messages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.titova.vkr.db.ChatMessage
import app.titova.vkr.view_model.MessengerViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun MessengerScreen(
    navController: NavHostController,
    chatId: String, // Параметр chatId должен быть передан
    viewModel: MessengerViewModel
) {
    val messages by viewModel.getMessagesForChat(chatId).collectAsState() // Используем chatId для получения сообщений
    var newMessage by remember { mutableStateOf("") }
    val currentUser = ""
    val receiver = "User B"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Мессенджер", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                MessageItem(message, currentUser)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = newMessage,
            onValueChange = { newMessage = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.padding(8.dp)) {
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.sendMessage(newMessage, currentUser, receiver, chatId)
            newMessage = ""
        }) {
            Text("Отправить")
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage, currentUser: String) {
    val isSentByUser = message.sender == currentUser
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .let {
                if (isSentByUser) it.padding(start = 40.dp) else it.padding(end = 40.dp)
            }
    ) {
        Text(
            text = message.message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSentByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${message.time}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
