package app.titova.vkr.view.app_activity

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.titova.vkr.db.Task
import app.titova.vkr.db.User
import app.titova.vkr.view_model.TaskViewModel
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.database.*
import java.util.*

@Composable
fun TaskScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel
) {
    var newTaskTitle by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskDescription by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskAssignee by remember { mutableStateOf("Выберите исполнителя") }
    var newTaskStatus by remember { mutableStateOf("To Do") } // Статус задачи
    var newTaskStartTime by remember { mutableStateOf("") }
    var newTaskEndTime by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Admin") }

    // Состояние для отображения диалогового окна
    var isDialogOpen by remember { mutableStateOf(false) }

    // Collect the tasks state from the ViewModel
    val tasks by taskViewModel.tasks.collectAsState()

    // Список возможных статусов задачи
    val taskStatuses = listOf("To Do", "In Progress", "Completed")

    // Загружаем список пользователей из Firebase
    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        loadUsersFromFirebase(users)
    }

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
            TaskItem(task, role, taskViewModel, users)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Если роль Admin, показываем кнопку "Добавить задачу"
        if (role == "Admin") {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("TaskScreen", "Add task button clicked") // Лог для проверки клика
                        isDialogOpen = true
                    },
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
            }
        }
    }

    // Отображение диалогового окна для добавления задачи
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.text.isNotBlank() && newTaskDescription.text.isNotBlank()) {
                            val newTask = Task(
                                id = (tasks.size + 1).toString(),
                                name = newTaskTitle.text,
                                description = newTaskDescription.text,
                                ispolnitel = newTaskAssignee,
                                header = "Admin",
                                status = newTaskStatus,
                                time_start = newTaskStartTime,
                                time_end = newTaskEndTime
                            )
                            Log.d("TaskScreen", "New task created: ${newTask.name}") // Лог при создании задачи
                            taskViewModel.addTask(newTask)

                            // Очистка полей после добавления задачи
                            newTaskTitle = TextFieldValue("")
                            newTaskDescription = TextFieldValue("")
                            newTaskAssignee = "Выберите исполнителя"
                            newTaskStatus = "To Do"
                            newTaskStartTime = ""
                            newTaskEndTime = ""

                            // Закрытие диалога
                            isDialogOpen = false
                        }
                    }
                ) {
                    Text("Добавить задачу")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { isDialogOpen = false }) {
                    Text("Отменить")
                }
            },
            title = {
                Text(text = "Добавить новую задачу", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
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

                    // Выпадающий список для выбора исполнителя
                    DropdownAssigneeMenu(
                        selectedAssignee = newTaskAssignee,
                        onAssigneeSelected = { newTaskAssignee = it },
                        userList = users
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Выпадающий список для выбора статуса задачи
                    DropdownStatusMenu(
                        selectedStatus = newTaskStatus,
                        onStatusSelected = { newTaskStatus = it },
                        statusList = taskStatuses
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Поле для выбора даты начала
                    DatePickerField(
                        label = "Дата начала",
                        selectedDate = newTaskStartTime,
                        onDateSelected = { newTaskStartTime = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Поле для выбора даты окончания
                    DatePickerField(
                        label = "Дата окончания",
                        selectedDate = newTaskEndTime,
                        onDateSelected = { newTaskEndTime = it }
                    )
                }
            }
        )
    }
}

// Функция загрузки данных из Firebase
fun loadUsersFromFirebase(userList: MutableList<User>) {
    val database = FirebaseDatabase.getInstance("https://titova-620c2-default-rtdb.firebaseio.com")
    val usersRef = database.getReference("Users")

    usersRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            userList.clear()
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                user?.let { userList.add(it) }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Failed to load users: ${error.message}")
        }
    })
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

// Выпадающий список для выбора исполнителя
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownAssigneeMenu(
    selectedAssignee: String,
    onAssigneeSelected: (String) -> Unit,
    userList: List<User>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedAssignee,
            onValueChange = {},
            readOnly = true,
            label = { Text("Исполнитель") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            userList.forEach { user ->
                DropdownMenuItem(
                    text = { Text("${user.name} ${user.surname}") },
                    onClick = {
                        onAssigneeSelected("${user.name} ${user.surname}")
                        expanded = false
                    }
                )
            }
        }
    }
}

// Выпадающий список для статуса задачи
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownStatusMenu(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    statusList: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedStatus,
            onValueChange = {},
            readOnly = true,
            label = { Text("Статус задачи") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusList.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Поле для выбора исполнителя задачи при "Назначить задачу"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskDropdown(
    task: Task,
    taskViewModel: TaskViewModel,
    users: List<User>,
    onDismissRequest: () -> Unit // Добавим коллбэк для скрытия
) {
    var selectedAssignee by remember { mutableStateOf(task.ispolnitel) }
    var expanded by remember { mutableStateOf(true) } // Поле видно при инициализации

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedAssignee,
            onValueChange = {},
            readOnly = true,
            label = { Text("Назначить исполнителя") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text("${user.name} ${user.surname}") },
                    onClick = {
                        selectedAssignee = "${user.name} ${user.surname}"
                        taskViewModel.assignTask(task.id, selectedAssignee)
                        expanded = false // Скрыть меню после выбора
                        onDismissRequest() // Вызвать функцию скрытия
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    role: String,
    taskViewModel: TaskViewModel,
    users: List<User>
) {
    var isDropdownVisible by remember { mutableStateOf(false) }
    var isStatusDropdownVisible by remember { mutableStateOf(false) }
    val statuses = listOf("В ожидание", "В работе", "Выполнено") // Статусы задачи

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

                Row(){
                // Кнопка "Назначить задачу"
                Button(
                    onClick = { isDropdownVisible = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Исполнитель", color = Color.White, fontSize = 14.sp)
                }

                if (isDropdownVisible) {
                    // Выпадающий список исполнителей
                    AssignTaskDropdown(
                        task = task,
                        taskViewModel = taskViewModel,
                        users = users,
                        onDismissRequest = { isDropdownVisible = false }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка для выбора статуса
                Button(
                    onClick = { isStatusDropdownVisible = true },
                    modifier = Modifier.padding(start =  16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cтатус", color = Color.White, fontSize = 14.sp)
                }

                if (isStatusDropdownVisible) {
                    // Выпадающий список для статуса задачи
                    StatusDropdownMenu(
                        selectedStatus = task.status,
                        onStatusSelected = { newStatus ->
                            taskViewModel.updateTaskStatus(task.id, newStatus) // Обновление статуса задачи
                            isStatusDropdownVisible = false // Скрыть выпадающий список после выбора
                        },
                        statusList = statuses
                    )
                }
            } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdownMenu(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    statusList: List<String>
) {
    var expanded by remember { mutableStateOf(true) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedStatus,
            onValueChange = {},
            readOnly = true,
            label = { Text("Статус задачи") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusList.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false // Скрыть меню после выбора
                    }
                )
            }
        }
    }
}



@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // DatePickerDialog для выбора даты
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Форматирование выбранной даты (например: 2024-02-21)
            val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            onDateSelected(formattedDate)
        }, year, month, day
    )

    // Отображение поля для выбора даты
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RectangleShape)
            .padding(16.dp)
            .clickable { datePickerDialog.show() }
    ) {
        Text(
            text = if (selectedDate.isNotEmpty()) selectedDate else label,
            color = if (selectedDate.isNotEmpty()) Color.Black else Color.Gray
        )
    }
}

