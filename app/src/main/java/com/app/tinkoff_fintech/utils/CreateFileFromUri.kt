package com.app.tinkoff_fintech.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CancellationException
import javax.inject.Inject

class CreateFileFromUri @Inject constructor(private val context: Context) {
    fun execute(uri: Uri?): File? {
        if (uri == null)
            return null
        val typeFile: String?
        val nameFile: String?
        if (uri.path != null && uri.path?.split(".")?.size!! > 1) {
            nameFile = uri.path?.split("/")?.last()
            typeFile = uri.path?.split(".")?.get(1)?.substring(0, 3)
        } else {
            nameFile = "file_" + System.currentTimeMillis()
            typeFile = "doc"
        }
        val file = File(
            context.getExternalFilesDir(null),
            "$nameFile.$typeFile"
        )
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(1024)
                var read: Int = input.read(buffer)
                while (read != -1) {
                    try {
                        Thread.yield()
                        output.write(buffer, 0, read)
                        read = input.read(buffer)
                    } catch (e: CancellationException) {
                        file.deleteRecursively()
                    }
                }
            }
        }
        return file
    }
}