package com.app.homework_5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.detail_activity.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_GROUP_NAME = "arg_group_name"
        const val ARG_CONTENT_IMAGE = "arg_content_image"
        const val ARG_CONTENT_TEXT = "arg_content_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        name.text = intent.getStringExtra(ARG_GROUP_NAME)
        postContent.text = intent.getStringExtra(ARG_CONTENT_TEXT)
        supportStartPostponedEnterTransition()
        ImageLoader().glideLoad(this, intent.getStringExtra(ARG_CONTENT_IMAGE), postImage)
        postImage.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                postImage,
                ViewCompat.getTransitionName(postImage)!!
            )
            startActivity(Intent(this, ImageActivity::class.java).apply {
                putExtra(ARG_CONTENT_IMAGE, intent.getStringExtra(ARG_CONTENT_IMAGE))
            }, options.toBundle()) }
    }
}