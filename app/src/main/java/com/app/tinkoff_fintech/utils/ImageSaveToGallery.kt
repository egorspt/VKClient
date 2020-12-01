package com.app.tinkoff_fintech.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.IOException
import javax.inject.Inject

class ImageSaveToGallery @Inject constructor(private val contentResolver: ContentResolver) {

    fun execute(bitmap: Bitmap): Boolean {
        val relativeLocation =
            Environment.DIRECTORY_PICTURES + File.pathSeparator + "tfs"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let { uri ->
                val stream = contentResolver.openOutputStream(uri)

                stream?.let { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                        throw IOException("Failed to save bitmap.")
                    } else {
                        return true
                    }
                } ?: throw IOException("Failed to get output stream.")

            } ?: throw IOException("Failed to create new MediaStore record")

        } catch (e: IOException) {
            if (uri != null) {
                contentResolver.delete(uri, null, null)
            }
            return false
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            }
        }
    }

}