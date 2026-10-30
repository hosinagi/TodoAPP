package com.hosinagi.todoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hosinagi.todoapp.utils.AvatarStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AvatarStoreTest {

    @Test
    fun avatarIsSavedPerUserAndCanBeReplaced() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val suffix = System.nanoTime()
        val firstUser = "avatar_a_$suffix"
        val secondUser = "avatar_b_$suffix"
        val sourceFile = File(context.cacheDir, "avatar_source_$suffix.jpg")
        val firstAvatar = AvatarStore.getAvatarFile(context, firstUser)
        val secondAvatar = AvatarStore.getAvatarFile(context, secondUser)

        try {
            sourceFile.writeBytes(byteArrayOf(1, 2, 3))
            assertTrue(AvatarStore.saveFromFile(context, firstUser, sourceFile))
            assertEquals(3L, firstAvatar.length())
            assertFalse(secondAvatar.exists())

            sourceFile.writeBytes(byteArrayOf(4, 5))
            assertTrue(AvatarStore.saveFromFile(context, firstUser, sourceFile))
            assertEquals(2L, firstAvatar.length())
        } finally {
            sourceFile.delete()
            firstAvatar.delete()
            secondAvatar.delete()
        }
    }
}
