package com.hosinagi.todoapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosinagi.todoapp.R
import com.hosinagi.todoapp.app.db.DataBaseHelper
import com.hosinagi.todoapp.app.db.Todo
import com.hosinagi.todoapp.databinding.FragmentTodoListBinding
import com.hosinagi.todoapp.databinding.DialogTodoEditBinding
import com.hosinagi.todoapp.ui.activity.LoginActivity
import com.hosinagi.todoapp.ui.adapter.TodoAdapter
import com.hosinagi.todoapp.utils.SessionManager
import es.dmoral.toasty.Toasty

class TodoListFragment : Fragment() {
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DataBaseHelper
    private lateinit var todoAdapter: TodoAdapter
    private var allTodos: List<Todo> = emptyList()
    private var selectedFilter = TodoFilter.ALL

    private enum class TodoFilter {
        ALL,
        PENDING,
        COMPLETED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = SessionManager.getUsername(requireContext())
        if (username == null) {
            openLoginPage()
            return
        }

        dbHelper = DataBaseHelper(requireContext())
        setupTodoList()
        setupListeners()
        loadTodos(username)
    }

    override fun onDestroyView() {
        binding.todoList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun setupTodoList() {
        todoAdapter = TodoAdapter(
            onCompletedChange = { todo, completed ->
                updateCompleted(todo, completed)
            },
            onEdit = { todo ->
                showTodoEditor(todo)
            },
            onDelete = { todo ->
                confirmDelete(todo)
            }
        )
        binding.todoList.layoutManager = LinearLayoutManager(requireContext())
        binding.todoList.adapter = todoAdapter
    }

    private fun setupListeners() {
        binding.filterGroup.check(R.id.filterAll)
        binding.filterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            selectedFilter = when (checkedId) {
                R.id.filterPending -> TodoFilter.PENDING
                R.id.filterCompleted -> TodoFilter.COMPLETED
                else -> TodoFilter.ALL
            }
            renderTodos()
        }
        binding.addTodoButton.setOnClickListener {
            showTodoEditor(null)
        }
    }

    private fun loadTodos() {
        val username = SessionManager.getUsername(requireContext()) ?: return
        loadTodos(username)
    }

    private fun loadTodos(username: String) {
        Thread {
            val todos = dbHelper.queryTodos(username)
            postToUi {
                allTodos = todos
                renderTodos()
            }
        }.start()
    }

    private fun renderTodos() {
        if (_binding == null) return
        val visibleTodos = when (selectedFilter) {
            TodoFilter.ALL -> allTodos
            TodoFilter.PENDING -> allTodos.filterNot { it.completed }
            TodoFilter.COMPLETED -> allTodos.filter { it.completed }
        }
        todoAdapter.submitItems(visibleTodos)
        binding.emptyState.isVisible = visibleTodos.isEmpty()
        binding.todoList.isVisible = visibleTodos.isNotEmpty()
    }

    private fun showTodoEditor(todo: Todo?) {
        val dialogBinding = DialogTodoEditBinding.inflate(layoutInflater)
        dialogBinding.titleInput.setText(todo?.title.orEmpty())
        dialogBinding.contentInput.setText(todo?.content.orEmpty())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (todo == null) "新增待办" else "编辑待办")
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .setPositiveButton("保存", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = dialogBinding.titleInput.text.toString().trim()
                val content = dialogBinding.contentInput.text.toString().trim()
                if (title.isEmpty()) {
                    dialogBinding.titleLayout.error = "请输入待办标题"
                    return@setOnClickListener
                }
                dialogBinding.titleLayout.error = null
                dialog.dismiss()

                val username = SessionManager.getUsername(requireContext()) ?: return@setOnClickListener
                mutateTodo {
                    if (todo == null) {
                        dbHelper.insertTodo(username, title, content)
                    } else {
                        dbHelper.updateTodo(todo.copy(title = title, content = content))
                    }
                }
            }
        }
        dialog.show()
    }

    private fun updateCompleted(todo: Todo, completed: Boolean) {
        val username = SessionManager.getUsername(requireContext()) ?: return
        mutateTodo {
            dbHelper.updateTodoCompleted(todo.id, username, completed)
        }
    }

    private fun confirmDelete(todo: Todo) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除待办")
            .setMessage("确定删除“${todo.title}”吗？")
            .setNegativeButton("取消", null)
            .setPositiveButton("删除") { _, _ ->
                val username = SessionManager.getUsername(requireContext()) ?: return@setPositiveButton
                mutateTodo {
                    dbHelper.deleteTodo(todo.id, username)
                }
            }
            .show()
    }

    private fun mutateTodo(operation: () -> Boolean) {
        Thread {
            val succeeded = operation()
            postToUi {
                if (succeeded) {
                    loadTodos()
                } else {
                    loadTodos()
                    Toasty.error(
                        requireContext(),
                        "操作失败，请稍后重试",
                        Toasty.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun postToUi(action: () -> Unit) {
        activity?.runOnUiThread {
            if (isAdded && _binding != null) {
                action()
            }
        }
    }

    private fun openLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
