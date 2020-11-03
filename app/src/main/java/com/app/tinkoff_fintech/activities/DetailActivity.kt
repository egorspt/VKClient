package com.app.tinkoff_fintech.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.app.tinkoff_fintech.ImageLoader
import com.app.tinkoff_fintech.NetworkService
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.DatabaseService
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.android.synthetic.main.post_layout.view.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_ID_POST = "arg_id_post"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)
        supportActionBar?.title = getString(R.string.titleDetail)

        val idPost = intent.getIntExtra(ARG_ID_POST, 0)
        DatabaseService(this).defaultDatabase().postDao().findById(idPost)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy( { error ->
                val message = error.message
            }, {post ->
                fillPost(post)
            }
            )

        supportStartPostponedEnterTransition()

        postLayout.contentImage.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                postLayout.contentImage,
                ViewCompat.getTransitionName(postLayout.contentImage)!!
            )
            startActivity(Intent(this, ImageActivity::class.java).apply {
                putExtra(ARG_ID_POST, idPost)
            }, options.toBundle())
        }

    }

    private fun fillPost(post: Post) {
        with(postLayout) {
            contentText.text = post.text
            ownerName.text = post.ownerName
            setDatePost(post.date)
            Glide.with(this)
                .load(post.ownerImage)
                .into(ownerImage)
            countLikes.text = post.likes.count.toString()
            countComments.text = post.comments.count.toString()
            setIsLiked(post.likes.userLikes == 1)
            buttonLike.setOnClickListener {
                changeLikes(post, isLiked())
                setIsLiked(!isLiked())
            }
            countLikes.setOnClickListener {
                changeLikes(post, isLiked())
                setIsLiked(!isLiked())
            }
        }
        ImageLoader().glideLoad(this, post.image, postLayout.contentImage)
    }

    private fun changeLikes(post: Post, isLikes: Boolean) {
        val vkService = NetworkService().create()

        if (!isLikes) {
            vkService.addLike(post.id, post.ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        } else
            vkService.deleteLike(post.id, post.ownerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}