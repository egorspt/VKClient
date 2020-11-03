package com.app.tinkoff_fintech.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.tinkoff_fintech.ImageLoader
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.activities.DetailActivity.Companion.ARG_ID_POST
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.image_activity.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImageActivity : AppCompatActivity() {
    private lateinit var post: Post
    val REQUEST_STORAGE_PERMISSION = 122

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorMaterialBlack)

        val idPost = intent.getIntExtra(ARG_ID_POST, 0)
        DatabaseService(this).defaultDatabase().postDao()
            .findById(idPost)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { post ->
                this.post = post
                ImageLoader().glideLoad(this, post.image, imageView)
            }

        download.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkForPermission()
            } else {
                saveImage()
            }
        }

        share.setOnClickListener { shareImage() }
    }

    private fun shareImage() {
        progressBar.visibility = View.VISIBLE
        Glide.with(this).asBitmap().load(post.image).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val bmpUri: Uri? = getLocalBitmapUri(resource)

                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND;
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
                shareIntent.type = "image/*"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "Share image"))
                progressBar.visibility = View.GONE
            }})
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "share_image_" + System.currentTimeMillis() + ".png"
        )
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            bmpUri = Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    private fun saveImage() {
        progressBar.visibility = View.VISIBLE
        val relativeLocation = Environment.DIRECTORY_PICTURES + File.pathSeparator + "TinkoffFintech"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = contentResolver
        Glide.with(this).asBitmap().load(post.image).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                try {
                    uri?.let { uri ->
                        val stream = resolver.openOutputStream(uri)

                        stream?.let { stream ->
                            if (!resource.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                                throw IOException("Failed to save bitmap.")
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@ImageActivity,
                                    "Фото сохранено",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } ?: throw IOException("Failed to get output stream.")

                    } ?: throw IOException("Failed to create new MediaStore record")

                } catch (e: IOException) {
                    if (uri != null) {
                        resolver.delete(uri, null, null)
                    }
                    throw IOException(e)
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else {
                    Toast.makeText(
                        this,
                        "R.string.can_not_save_image_need_permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
            saveImage()
        }
    }
}
