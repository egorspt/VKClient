package com.app.tinkoff_fintech.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import javax.inject.Inject

class ImageShare @Inject constructor(private val context: Context) {

    fun execute(bitmap: Bitmap) {
        val bmpUri: Uri? = getLocalBitmapUri(bitmap)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND;
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, bmpUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image"))
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            var out: FileOutputStream? = null
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}