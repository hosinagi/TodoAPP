package com.hosinagi.todoapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hosinagi.todoapp.app.db.DataBaseHelper

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbHelper = DataBaseHelper(context)
        val username = "user_${System.nanoTime()}"
        try {
            assertTrue(dbHelper.registerUser(username, "123456"))
            assertTrue(dbHelper.checkUserExists(username))
        } finally {
            dbHelper.deleteUser(username)
            dbHelper.close()
        }
    }
}
