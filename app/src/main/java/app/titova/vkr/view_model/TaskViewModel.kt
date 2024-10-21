package app.titova.vkr.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.titova.vkr.db.Task
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val database = FirebaseDatabase.getInstance().getReference("tasks")

    init {
        // Listen for changes in Firebase
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue(Task::class.java)
                if (task != null) {
                    _tasks.value = _tasks.value + task
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedTask = snapshot.getValue(Task::class.java)
                if (updatedTask != null) {
                    _tasks.value = _tasks.value.map { task ->
                        if (task.id == updatedTask.id) updatedTask else task
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedTask = snapshot.getValue(Task::class.java)
                if (removedTask != null) {
                    _tasks.value = _tasks.value.filter { it.id != removedTask.id }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            database.child(task.id).setValue(task)
        }
    }
    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            val taskRef = database.child(taskId) // Найдем задачу по ID
            taskRef.child("status").setValue(newStatus) // Обновим поле "status" в Firebase
        }
    }
    fun assignTask(taskId: String, newAssignee: String) {
        viewModelScope.launch {
            val taskRef = database.child(taskId)
            taskRef.child("ispolnitel").setValue(newAssignee)
        }
    }
}
