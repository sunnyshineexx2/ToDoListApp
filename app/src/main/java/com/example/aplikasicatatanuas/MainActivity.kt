package com.example.aplikasicatatanuas

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var addButton: Button
    private lateinit var taskContainer: LinearLayout
    private lateinit var emptyState: TextView
    private lateinit var filterAll: Button
    private lateinit var filterActive: Button
    private lateinit var filterCompleted: Button
    private lateinit var tasksCount: TextView

    private val tasks = mutableListOf<Task>()
    private var currentFilter = FilterType.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskInput = findViewById(R.id.task_input)
        addButton = findViewById(R.id.add_button)
        taskContainer = findViewById(R.id.task_container)
        emptyState = findViewById(R.id.empty_state)
        filterAll = findViewById(R.id.filter_all)
        filterActive = findViewById(R.id.filter_active)
        filterCompleted = findViewById(R.id.filter_completed)
        tasksCount = findViewById(R.id.tasks_count)

        addButton.setOnClickListener { addTask() }
        filterAll.setOnClickListener { setFilter(FilterType.ALL) }
        filterActive.setOnClickListener { setFilter(FilterType.ACTIVE) }
        filterCompleted.setOnClickListener { setFilter(FilterType.COMPLETED) }

        updateUI()
    }

    private fun addTask() {
        val taskText = taskInput.text.toString().trim()
        if (taskText.isEmpty()) {
            Snackbar.make(taskInput.rootView, "Task cannot be empty", Snackbar.LENGTH_SHORT).show()
            return
        }

        tasks.add(Task(taskText, false))
        taskInput.text.clear()
        updateUI()
    }

    private fun setFilter(filterType: FilterType) {
        currentFilter = filterType
        filterAll.isSelected = filterType == FilterType.ALL
        filterActive.isSelected = filterType == FilterType.ACTIVE
        filterCompleted.isSelected = filterType == FilterType.COMPLETED
        updateUI()
    }

    private fun updateUI() {
        taskContainer.removeAllViews()

        val filteredTasks = when (currentFilter) {
            FilterType.ALL -> tasks
            FilterType.ACTIVE -> tasks.filter { !it.completed }
            FilterType.COMPLETED -> tasks.filter { it.completed }
        }

        emptyState.isVisible = filteredTasks.isEmpty()

        filteredTasks.forEach { task ->
            val taskView = LayoutInflater.from(this).inflate(R.layout.task_item, taskContainer, false)

            val taskText = taskView.findViewById<TextView>(R.id.task_text)
            val taskCheckbox = taskView.findViewById<CheckBox>(R.id.task_checkbox)
            val deleteButton = taskView.findViewById<Button>(R.id.delete_button)

            taskText.text = task.text
            taskCheckbox.isChecked = task.completed
            taskText.paintFlags = if (task.completed)
                taskText.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            else
                taskText.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()

            taskCheckbox.setOnCheckedChangeListener(null) // Prevent recursive trigger
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                task.completed = isChecked
                updateUI()
            }

            deleteButton.setOnClickListener {
                tasks.remove(task)
                updateUI()
                Snackbar.make(taskContainer.rootView, "Task deleted", Snackbar.LENGTH_SHORT).show()
            }

            taskContainer.addView(taskView)
        }

        val activeCount = tasks.count { !it.completed }
        tasksCount.text = "$activeCount active tasks"
    }

    data class Task(val text: String, var completed: Boolean)

    enum class FilterType {
        ALL, ACTIVE, COMPLETED
    }
}
