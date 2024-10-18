package app.titova.vkr.view.app_activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.titova.vkr.db.Task
import app.titova.vkr.view_model.TaskViewModel
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TaskScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel
) {
    var newTaskTitle by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskDescription by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskAssignee by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskStatus by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskStartTime by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskEndTime by remember { mutableStateOf(TextFieldValue("")) }
    var role by remember { mutableStateOf("Admin") }

    // Состояние для отображения формы
    var isFormVisible by remember { mutableStateOf(false) }

    // Collect the tasks state from the ViewModel
    val tasks by taskViewModel.tasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Brush.verticalGradient(colors = listOf(Color.White, Color.LightGray)))
    ) {
        item {
            Text(
                "Задачи",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Список задач
        items(tasks) { task ->
            TaskItem(task, role, taskViewModel)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Если роль Admin, показываем кнопку "Добавить задачу"
        if (role == "Admin") {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isFormVisible = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Добавить задачу",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Если форма видима, отображаем ёё
                if (isFormVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Добавить новую задачу",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskTitle,
                                onValueChange = { newTaskTitle = it },
                                placeholder = "Введите название задачи",
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskDescription,
                                onValueChange = { newTaskDescription = it },
                                placeholder = "Введите описание задачи",
                                modifier = Modifier.height(80.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskAssignee,
                                onValueChange = { newTaskAssignee = it },
                                placeholder = "Введите исполнителя задачи",
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskStatus,
                                onValueChange = { newTaskStatus = it },
                                placeholder = "Введите статус задачи",
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskStartTime,
                                onValueChange = { newTaskStartTime = it },
                                placeholder = "Введите дату начала (YYYY-MM-DD)",
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            TaskInputField(
                                value = newTaskEndTime,
                                onValueChange = { newTaskEndTime = it },
                                placeholder = "Введите дату окончания (YYYY-MM-DD)",
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    onClick = {
                                        if (newTaskTitle.text.isNotBlank() && newTaskDescription.text.isNotBlank()) {
                                            val newTask = Task(
                                                id = (tasks.size + 1).toString(),
                                                name = newTaskTitle.text,
                                                description = newTaskDescription.text,
                                                ispolnitel = newTaskAssignee.text,
                                                header = "Admin",
                                                status = newTaskStatus.text,
                                                time_start = newTaskStartTime.text,
                                                time_end = newTaskEndTime.text
                                            )
                                            taskViewModel.addTask(newTask)

                                            newTaskTitle = TextFieldValue("")
                                            newTaskDescription = TextFieldValue("")
                                            newTaskAssignee = TextFieldValue("")
                                            newTaskStatus = TextFieldValue("")
                                            newTaskStartTime = TextFieldValue("")
                                            newTaskEndTime = TextFieldValue("")

                                            isFormVisible = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Create Task", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { isFormVisible = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color.Gray, RectangleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun TaskItem(task: Task, role: String, taskViewModel: TaskViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("${task.name}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${task.description}", style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Исполнитель: ${task.ispolnitel}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text("Статус: ${task.status}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)

            if (role == "Admin") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        taskViewModel.assignTask(task.id, "User A")
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Назначить задачу", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}