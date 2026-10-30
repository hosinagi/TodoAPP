package com.hosinagi.todoapp.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.hosinagi.todoapp.app.db.Todo
import com.hosinagi.todoapp.databinding.ItemTodoBinding

class TodoAdapter(
    private val onCompletedChange: (Todo, Boolean) -> Unit,
    private val onEdit: (Todo) -> Unit,
    private val onDelete: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val items = mutableListOf<Todo>()

    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = items[position]
        with(holder.binding) {
            completedCheck.setOnCheckedChangeListener(null)
            completedCheck.isChecked = todo.completed
            completedCheck.setOnCheckedChangeListener { _, isChecked ->
                onCompletedChange(todo, isChecked)
            }

            todoTitle.text = todo.title
            val strikeThrough = Paint.STRIKE_THRU_TEXT_FLAG
            todoTitle.paintFlags = if (todo.completed) {
                todoTitle.paintFlags or strikeThrough
            } else {
                todoTitle.paintFlags and strikeThrough.inv()
            }
            todoTitle.alpha = if (todo.completed) 0.62f else 1f

            todoContent.text = todo.content
            todoContent.isVisible = todo.content.isNotBlank()
            todoContent.alpha = if (todo.completed) 0.62f else 1f

            root.setOnClickListener { onEdit(todo) }
            deleteButton.setOnClickListener { onDelete(todo) }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitItems(newItems: List<Todo>) {
        val oldItems = items.toList()
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        })
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}
