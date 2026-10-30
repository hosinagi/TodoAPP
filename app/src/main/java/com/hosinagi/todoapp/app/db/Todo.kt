package com.hosinagi.todoapp.app.db

data class Todo(
    val id: Int,
    val username: String,
    val title: String,
    val content: String,
    val completed: Boolean,
    val createdAt: Long
)
