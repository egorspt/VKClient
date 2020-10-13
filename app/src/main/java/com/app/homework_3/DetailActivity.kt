package com.app.homework_3

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.detail_activity.*

const val ARG_GROUP_NAME = "arg_group_name"
const val ARG_CONTENT_IMAGE = "arg_content_image"
const val ARG_CONTENT_TEXT = "arg_content_text"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        name.text = intent.getStringExtra(ARG_GROUP_NAME)
        postContent.text = intent.getStringExtra(ARG_CONTENT_TEXT)
        Glide.with(this)
            .load(intent.getStringExtra(ARG_CONTENT_IMAGE))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }
            })
            .into(postImage)
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