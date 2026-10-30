package com.hosinagi.todoapp.app.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, NAME, null, VERSION) {
    // 创建伴生类写死属性
    companion object {
        private val NAME = "Todo.db"
        private val VERSION = 2

        // 用户表和表的字段
        private const val USER_TABLE_NAME = "user"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_PASSWORD = "password"

        // 待办表和表的字段
        private const val TODO_TABLE_NAME = "todo"
        private const val TODO_ID = "id"
        private const val TODO_USERNAME = "username"
        private const val TODO_TITLE = "title"
        private const val TODO_CONTENT = "content"
        private const val TODO_COMPLETED = "completed"
        private const val TODO_CREATED_AT = "created_at"

        // 定义创建用户表的SQL语句
        private const val SQL_CREATE_USER = "CREATE TABLE $USER_TABLE_NAME($USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $USER_NAME varchar(20) UNIQUE NOT NULL, $USER_PASSWORD varchar(20) NOT NULL)"
        private const val SQL_CREATE_TODO = "CREATE TABLE IF NOT EXISTS $TODO_TABLE_NAME(" +
            "$TODO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$TODO_USERNAME TEXT NOT NULL, " +
            "$TODO_TITLE TEXT NOT NULL, " +
            "$TODO_CONTENT TEXT NOT NULL DEFAULT '', " +
            "$TODO_COMPLETED INTEGER NOT NULL DEFAULT 0, " +
            "$TODO_CREATED_AT INTEGER NOT NULL)"
    }
    // 创建数据库
    override fun onCreate(db: SQLiteDatabase?) {
        // 执行SQL语句创建数据库
        db?.execSQL(SQL_CREATE_USER)
        db?.execSQL(SQL_CREATE_TODO)
    }
    // 数据库升级策略
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL(SQL_CREATE_TODO)
        }
    }

    // 数据库注册用户方法，用于向数据库中插入用户信息
    fun registerUser(name: String, password: String): Boolean {
        val db = writableDatabase // writable表示写入数据库
        // 创建ContentValues对象，用于封装要插入的数据
        val values = ContentValues()
        // 把外部传入的name和password封装到ContentValues中，对应名称
        values.put(USER_NAME, name)
        values.put(USER_PASSWORD, password)

        // 执行插入操作，返回插入成功的行数，一般都为1
        val result = db.insert(USER_TABLE_NAME, null, values)
        // 插入完成后要关闭数据库，否则内存泄漏
        db.close()
        return result != -1L // 正常返回都为1，不等于1则表示插入失败
    }

    // 数据库查询用户方法，用于判断用户名是否已经存在
    fun checkUserExists(name: String): Boolean {
        // readable表示创建读取数据库的句柄
        val db = readableDatabase
        // 查询用户名是否存在,query：查询语句
        val cursor = db.query(
            USER_TABLE_NAME, // 要查询的表名
            arrayOf(USER_NAME), // 要查询的列,这里是用户名
            "$USER_NAME = ?", // 查询条件where,?号表示占位符
            arrayOf(name), // 传入要查询的数据，可以查询多个数据，有几个？就可以查询几个，按数组顺序查询
            null,
            null,
            null
        )
        // 判断cursor的行数是否大于0，大于0表示查询到数据，即用户名存在
        val result = cursor.count > 0
        // 关闭数据库和cursor
        db.close()
        cursor.close()
        return result
    }

    // 数据库删除数据方法，删除已存在的用户
    fun deleteUser(name: String): String {
        // 判断用户名是否存在，存在才能删除
        if(checkUserExists(name)) {
            val db = writableDatabase
            // 执行删除操作，返回删除的行数
            val result = db.delete(USER_TABLE_NAME, "$USER_NAME = ?", arrayOf(name))
            db.close()
            // if判断删除的行数是否大于0，大于0表示删除成功
            return if (result > 0) "删除成功" else "删除失败"
        }else{
            return "用户名不存在"
        }
    }

    // 数据库更新数据方法，更新已存在的用户信息
    fun updateUser(name: String, newPassword: String): String {
        // 判断用户名是否存在，存在才能更新
        if(checkUserExists(name)) {
            // 获取可写数据库
            val db = writableDatabase
            // 创建ContentValues对象，用于封装要更新的数据
            val values = ContentValues()
            values.put(USER_PASSWORD, newPassword)
            // 执行更新操作，返回更新的行数
            val result = db.update(USER_TABLE_NAME, values, "$USER_NAME = ?", arrayOf(name))
            db.close()
            // if判断更新的行数是否大于0，大于0表示更新成功
            return if (result > 0) "更新成功" else "更新失败"
        }else{
            return "用户名不存在"
        }
    }

    @SuppressLint("Range")
    // 查询所有用户信息
    fun queryAllUsers(): List<User> {
        // 查询获取可读数据库句柄
        val db = readableDatabase
        // 创建游标对象存储数据，不需要分组，判断条件
        val cursor = db.query(
            USER_TABLE_NAME,
            arrayOf(USER_ID, USER_NAME, USER_PASSWORD),
            null,
            null,
            null,
            null,
            null
        )
        // 创建可变列表存储用户数据，泛型为User数据类
        val users = mutableListOf<User>()
        // 使用moveToNext遍历，有下一个数据就继续执行，直到没有数据为止
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(USER_ID))
            val name = cursor.getString(cursor.getColumnIndex(USER_NAME))
            val password = cursor.getString(cursor.getColumnIndex(USER_PASSWORD))
            // 将获取到的数据封装为User，并添加到列表中
            users.add(User(id, name, password))
        }
        cursor.close()
        db.close()
        return users
    }

    // 查询当前用户的待办，未完成的排在前面
    @Synchronized
    fun queryTodos(username: String): List<Todo> {
        val db = readableDatabase
        val cursor = db.query(
            TODO_TABLE_NAME,
            arrayOf(TODO_ID, TODO_USERNAME, TODO_TITLE, TODO_CONTENT, TODO_COMPLETED, TODO_CREATED_AT),
            "$TODO_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            "$TODO_COMPLETED ASC, $TODO_CREATED_AT DESC"
        )
        val todos = mutableListOf<Todo>()
        cursor.use {
            while (it.moveToNext()) {
                todos.add(
                    Todo(
                        id = it.getInt(it.getColumnIndexOrThrow(TODO_ID)),
                        username = it.getString(it.getColumnIndexOrThrow(TODO_USERNAME)),
                        title = it.getString(it.getColumnIndexOrThrow(TODO_TITLE)),
                        content = it.getString(it.getColumnIndexOrThrow(TODO_CONTENT)),
                        completed = it.getInt(it.getColumnIndexOrThrow(TODO_COMPLETED)) != 0,
                        createdAt = it.getLong(it.getColumnIndexOrThrow(TODO_CREATED_AT))
                    )
                )
            }
        }
        db.close()
        return todos
    }

    // 新增一条待办
    @Synchronized
    fun insertTodo(username: String, title: String, content: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TODO_USERNAME, username)
            put(TODO_TITLE, title)
            put(TODO_CONTENT, content)
            put(TODO_COMPLETED, 0)
            put(TODO_CREATED_AT, System.currentTimeMillis())
        }
        val result = db.insert(TODO_TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    // 修改待办标题和备注
    @Synchronized
    fun updateTodo(todo: Todo): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TODO_TITLE, todo.title)
            put(TODO_CONTENT, todo.content)
        }
        val result = db.update(
            TODO_TABLE_NAME,
            values,
            "$TODO_ID = ? AND $TODO_USERNAME = ?",
            arrayOf(todo.id.toString(), todo.username)
        )
        db.close()
        return result > 0
    }

    // 修改待办的完成状态
    @Synchronized
    fun updateTodoCompleted(id: Int, username: String, completed: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TODO_COMPLETED, if (completed) 1 else 0)
        }
        val result = db.update(
            TODO_TABLE_NAME,
            values,
            "$TODO_ID = ? AND $TODO_USERNAME = ?",
            arrayOf(id.toString(), username)
        )
        db.close()
        return result > 0
    }

    // 删除一条待办
    @Synchronized
    fun deleteTodo(id: Int, username: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TODO_TABLE_NAME,
            "$TODO_ID = ? AND $TODO_USERNAME = ?",
            arrayOf(id.toString(), username)
        )
        db.close()
        return result > 0
    }

    // 实现登录功能
    fun login(name: String, password: String): Boolean {
        // 获取可读数据库句柄
        val db = readableDatabase
        // 通过query的查询，查询用户信息是否存在，存在则登录成功，不存在则登录失败
        val cursor = db.query(
            USER_TABLE_NAME,
            arrayOf(USER_NAME, USER_PASSWORD),
            "$USER_NAME = ? AND $USER_PASSWORD = ?",
            arrayOf(name, password),
            null, null, null
        )
        // 可以通过count方法直接拿到已存数据行数
        val successFull = cursor.count > 0
        // 关闭游标
        cursor.close()
        db.close()
        return successFull
    }
}
