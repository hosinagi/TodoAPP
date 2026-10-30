package com.hosinagi.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hosinagi.todoapp.app.db.DataBaseHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    @Test
    fun todoCrudKeepsDataIsolatedByUsername() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbHelper = DataBaseHelper(context)
        val suffix = System.nanoTime()
        val firstUser = "todo_a_$suffix"
        val secondUser = "todo_b_$suffix"

        try {
            assertTrue(dbHelper.registerUser(firstUser, "123456"))
            assertTrue(dbHelper.registerUser(secondUser, "123456"))
            assertTrue(dbHelper.insertTodo(firstUser, "原始标题", "原始备注"))

            val todo = dbHelper.queryTodos(firstUser).single()
            assertEquals("原始标题", todo.title)
            assertEquals("原始备注", todo.content)
            assertFalse(todo.completed)
            assertTrue(dbHelper.queryTodos(secondUser).isEmpty())

            assertTrue(dbHelper.updateTodoCompleted(todo.id, firstUser, true))
            assertTrue(dbHelper.queryTodos(firstUser).single().completed)
            assertFalse(dbHelper.updateTodoCompleted(todo.id, secondUser, true))

            assertTrue(dbHelper.updateTodo(todo.copy(title = "修改标题", content = "修改备注")))
            val updatedTodo = dbHelper.queryTodos(firstUser).single()
            assertEquals("修改标题", updatedTodo.title)
            assertEquals("修改备注", updatedTodo.content)

            assertTrue(dbHelper.deleteTodo(todo.id, firstUser))
            assertTrue(dbHelper.queryTodos(firstUser).isEmpty())
        } finally {
            dbHelper.deleteUser(firstUser)
            dbHelper.deleteUser(secondUser)
            dbHelper.close()
        }
    }
}
