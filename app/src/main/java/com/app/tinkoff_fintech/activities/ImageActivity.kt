package com.app.tinkoff_fintech.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.tinkoff_fintech.ImageLoader
import com.app.tinkoff_fintech.Post
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.activities.DetailActivity.Companion.ARG_POST
import kotlinx.android.synthetic.main.image_activity.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_activity)
        supportActionBar?.title = ""

        ImageLoader()
            .glideLoad(this, (intent.getSerializableExtra(ARG_POST) as Post).image, imageView)
    }
}