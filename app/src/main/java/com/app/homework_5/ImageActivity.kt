package com.app.homework_5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.homework_5.DetailActivity.Companion.ARG_CONTENT_IMAGE
import kotlinx.android.synthetic.main.image_activity.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)

        ImageLoader().glideLoad(this, intent.getStringExtra(ARG_CONTENT_IMAGE), imageView)
    }
}