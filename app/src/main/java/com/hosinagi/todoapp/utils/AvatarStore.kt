package com.hosinagi.todoapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

object AvatarStore {
    fun getAvatarFile(context: Context, username: String): File {
        val fileName = "avatar_${Integer.toHexString(username.hashCode())}.jpg"
        return File(File(context.filesDir, "avatars"), fileName)
    }

    fun saveFromUri(context: Context, username: String, uri: Uri): Boolean {
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                save(context, username, input)
            } ?: false
        }.getOrDefault(false)
    }

    fun saveFromFile(context: Context, username: String, sourceFile: File): Boolean {
        return runCatching {
            sourceFile.inputStream().use { input ->
                save(context, username, input)
            }
        }.getOrDefault(false)
    }

    private fun save(context: Context, username: String, input: InputStream): Boolean {
        val avatarFile = getAvatarFile(context, username)
        avatarFile.parentFile?.mkdirs()
        val tempFile = File(avatarFile.parentFile, "${avatarFile.name}.tmp")
        return runCatching {
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
            tempFile.copyTo(avatarFile, overwrite = true)
            true
        }.also {
            tempFile.delete()
        }.getOrDefault(false)
    }
}
