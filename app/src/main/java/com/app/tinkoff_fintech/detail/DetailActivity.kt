package com.app.tinkoff_fintech.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.mvi.DetailMVIView
import com.app.tinkoff_fintech.detail.mvi.DetailState
import com.app.tinkoff_fintech.detail.paging.CommentLisViewModelFactory
import com.app.tinkoff_fintech.detail.paging.CommentListViewModel
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_activity.*
import javax.inject.Inject
import kotlin.properties.Delegates

open class DetailActivity : AppCompatActivity(), DetailMVIView {

    @Inject
    lateinit var detailAdapter: DetailAdapter
    private lateinit var viewModel: CommentListViewModel
    private var postId by Delegates.notNull<Int>()
    private var ownerId by Delegates.notNull<Int>()

    companion object {
        const val ARG_URL_IMAGE = "arg_id_post"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            super.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.titleDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        postId = intent.getIntExtra(ARG_URL_IMAGE, 0)
        getPost()

        editComment.addTextChangedListener(textWatcher)
        buttonSend.setOnClickListener { createComment() }

        /*val idPost = intent.getIntExtra(ARG_ID_POST, 0)
        DatabaseService(this).defaultDatabase().postDao().findById(idPost)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy({ error ->
                val message = error.message
            }, { post ->
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
        }*/

    }

    private fun createComment() {
        if (editComment.text.isEmpty()) return
        NetworkService.create()
            .createComment(ownerId, postId, editComment.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    showErrorCreateComment()
                },
                onSuccess = {
                    if (it.response == null)
                        showErrorCreateComment()
                    else successCreateComment()
                })
    }

    private fun successCreateComment() {
        viewModel.invalidate()
        editComment.hideKeyboard()
        editComment.clearFocus()
        editComment.text.clear()

    }

    private fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showErrorCreateComment() {
        Toast.makeText(this, "Не удалось отправить", Toast.LENGTH_SHORT).show()
    }

    private fun clickImage(url: String) {
        startActivity(Intent(this, ImageActivity::class.java).apply {
            putExtra(ARG_URL_IMAGE, url)
        })
    }

    private fun fillPost(post: Post) {
        /*with(postLayout) {
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
        ImageLoader()
            .glideLoad(this, post.image, postLayout.contentImage)
         */
    }

    private fun changeLikes(post: Post, isLikes: Boolean) {
        val vkService = NetworkService.create()

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

    private fun initAdapter(post: Post) {
        viewModel = ViewModelProviders.of(
            this,
            CommentLisViewModelFactory(post.ownerId, post.id)
        )
            .get(CommentListViewModel::class.java)
        initState()

        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_comment,
                null
            )!!
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        detailAdapter.clickImage = { url -> clickImage(url) }
        detailAdapter.retry = { viewModel.retry() }
        detailAdapter.post = post
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = detailAdapter

        viewModel.newsList.observe(this, Observer {
            detailAdapter.submitList(it)
        })
    }

    private fun initState() {
        textError.setOnClickListener { viewModel.retry() }
        viewModel.getState().observe(this, Observer { state ->
            progressBar.visibility =
                if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            textError.visibility =
                if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            detailAdapter.setState(state ?: State.DONE)

        })
    }

    private fun getPost() {
        DatabaseService(this)
            .defaultDatabase()
            .postDao()
            .findById(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy({ error ->
                val message = error.message
            }, { post ->
                ownerId = post.ownerId
                initAdapter(post)
            }
            )
    }

    override fun render(detailState: DetailState) {
        detailAdapter.post = detailState.post
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p3 > 0) buttonSend.setImageResource(R.drawable.ic_send_enable)
            else buttonSend.setImageResource(R.drawable.ic_send_disable)
        }
    }
}