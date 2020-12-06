package com.app.tinkoff_fintech.ui.views.activities

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.paging.detail.CommentListViewModel
import com.app.tinkoff_fintech.recycler.adapters.DetailAdapter
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import com.app.tinkoff_fintech.ui.presenters.DetailPresenter
import com.app.tinkoff_fintech.utils.State
import com.google.android.material.transition.platform.MaterialFade
import dagger.Lazy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_activity.*
import javax.inject.Inject
import kotlin.properties.Delegates

open class DetailActivity : AppCompatActivity(), DetailContractInterface.View {

    @Inject
    lateinit var presenter: DetailPresenter

    @Inject
    lateinit var detailAdapter: DetailAdapter

    @Inject
    @PostDatabase
    lateinit var postDatabase: Lazy<PostDao>

    @Inject
    @WallDatabase
    lateinit var wallDatabase: Lazy<PostDao>

    private val viewModel: CommentListViewModel by viewModels()
    private var postId by Delegates.notNull<Int>()
    private var ownerId by Delegates.notNull<Int>()
    private var fromActivity = 0

    companion object {
        const val ARG_OWNER_ID = "arg_owner_post"
        const val ARG_POST_ID = "arg_id_post"
        const val FROM_ACTIVITY = "from_activity"
        const val FROM_NEWS = 0
        const val FROM_PROFILE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.enterTransition = MaterialFade().apply {
            addTarget(R.id.containerEnter)
            duration = 600L
        }
        window.returnTransition = MaterialFade().apply {
            addTarget(R.id.containerEnter)
            duration = 400L
        }
        window.allowEnterTransitionOverlap = true

        (application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            super.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.titleDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fromActivity = intent.getIntExtra(FROM_ACTIVITY, 0)
        ownerId = intent.getIntExtra(ARG_OWNER_ID, 0)
        postId = intent.getIntExtra(ARG_POST_ID, 0)

        getPost()

        editComment.addTextChangedListener(textWatcher)
        buttonSend.setOnClickListener { createComment() }
    }

    override fun updateLikes(postId: Int, countLikes: Int, isLiked: Boolean) {
        val post = detailAdapter.post
        post.isLiked = isLiked
        post.countLikes = countLikes
        detailAdapter.notifyItemChanged(0, post)
    }

    private fun initViewModel() {
        if (!viewModel.isInitialized()) {
            (application as App).appComponent.inject(viewModel)
            viewModel.init(ownerId, postId)
        }

    }

    private fun createComment() {
        presenter.createComment(ownerId, postId, editComment.text.toString())
    }

    override fun updateComments() {
        viewModel.invalidate()
        editComment.hideKeyboard()
        editComment.clearFocus()
        editComment.text.clear()
    }

    private fun View.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun startImageActivity(url: String) {
        val intent = Intent(this, ImageActivity::class.java).apply {
            putExtra(ARG_POST_ID, url)
        }
        val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, bundle)
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLiked: Boolean) {
        presenter.changeLikes(postId, postOwnerId, isLiked)
    }

    private fun initAdapter(post: Post) {
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

        with(detailAdapter) {
            changeLikesListener =
                { postId, postOwnerId, isLiked -> changeLike(postId, postOwnerId, isLiked) }
            imageClickListener = { url -> startImageActivity(url) }
            retry = { viewModel.retry() }
            this.post = post
        }
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = detailAdapter

        viewModel.comments.observe(this, Observer {
            detailAdapter.submitList(it)
        })
    }

    private fun initState() {
        viewModel.getState().observe(this, Observer { state ->
            detailAdapter.setStateAdapter(state ?: State.DONE)
        })
    }

    private fun getPost() {
        val database = if (fromActivity == FROM_NEWS) postDatabase.get() else wallDatabase.get()
        database.find(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { },
                onSuccess = { post ->
                    if (ownerId == 0)
                        ownerId = post.ownerId
                    initViewModel()
                    initAdapter(post)
                }
            )
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