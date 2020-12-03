package com.app.tinkoff_fintech.ui.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.paging.news.NewsViewModel
import com.app.tinkoff_fintech.paging.news.PostsAdapter
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperCallback
import com.app.tinkoff_fintech.recyclerView.PostDecorator
import com.app.tinkoff_fintech.recyclerView.SwipeListener
import com.app.tinkoff_fintech.ui.contracts.NewsContractInterface
import com.app.tinkoff_fintech.ui.presenters.NewsPresenter
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.posts_fragment.*
import javax.inject.Inject


class AllPostsFragment : Fragment(), NewsContractInterface.View {

    @Inject
    lateinit var presenter: NewsPresenter
    @Inject
    lateinit var postsAdapter: PostsAdapter

    private val sharedModel: SharedViewModel by activityViewModels()
    private val viewModel: NewsViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        (activity?.applicationContext as App).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    private fun startDetailActivity(id: Int) {
        requireActivity().startActivity(Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, id)
        })
    }

    override fun onResume() {
        super.onResume()
        presenter.checkRelevanceNews()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
    }

    override fun updateNews() {
        textError.visibility = View.GONE
        viewModel.invalidate()
    }

    override fun init() {
        if (!viewModel.isInitialized()) {
            (activity?.application as App).appComponent.inject(viewModel)
            viewModel.init()
        }
        initState()
        initAdapter()
    }

    private fun initAdapter() {
        with(postsAdapter) {
            changeLikes = { itemId, ownerId, isLikes -> changeLike(itemId, ownerId, isLikes) }
            clickImage = { id -> startDetailActivity(id) }
            sharedViewModel = sharedModel
        }
        //fab.setOnClickListener { recyclerView.scrollToPosition(0); fab.visibility = View.GONE }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = postsAdapter
            addItemDecoration(PostDecorator())
            /*addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy >= 0) {
                        fab.visibility = View.GONE
                    } else {
                        fab.visibility = View.VISIBLE
                    }
                }
            })

             */
        }

        val callback =
            ItemTouchHelperCallback(postsAdapter as SwipeListener)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        swipeContainer.setOnRefreshListener {
            presenter.refreshNews()
        }

        viewModel.newsList.observe(requireActivity(), Observer<PagedList<Post>> { items ->
            postsAdapter.submitList(items)
        })
    }

    private fun initState() {
        textError.setOnClickListener { viewModel.retry() }
        viewModel.getState().observe(requireActivity(), Observer { state ->
            progressBar.visibility =
                if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE else View.GONE
            textError.visibility =
                if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty()) {
                hideShimmer()
                //postsAdapter.setState(state ?: State.DONE)
            }
        })
    }

    private fun showError(message: String) {
        /*if (message == "User authorization failed: access_token was given to another ip address.") {
            fragmentInteractor?.reLogin()
            return
        }
         */
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialogErrorTitle))
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialogPositiveButtonText)) { dialog, _ ->
                dialog.cancel()
            }.show()
        with(textError) {
            visibility = View.VISIBLE
            text = getString(R.string.errorText, message)
        }
        hideShimmer()
        swipeContainer.isRefreshing = false
    }

    override fun hideShimmer() {
        requireActivity().runOnUiThread {
            swipeContainer.isRefreshing = false
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
        }
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        presenter.changeLike(postId, postOwnerId, isLikes)
    }

    private fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, post: Post) {
        val arrayPairs = if (sharedImageView == null)
            arrayOf(Pair.create(sharedTextView as View, getString(R.string.transitionNameText)))
        else arrayOf(
            Pair.create(sharedImageView as View, getString(R.string.transitionNameImage)),
            Pair.create(sharedTextView as View, getString(R.string.transitionNameText))
        )
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), *arrayPairs)
        activity?.startActivity(Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, post.id)
        }, options.toBundle())
    }

    private fun initListeners() {
        viewModel.newsList.observe(requireActivity(), Observer<PagedList<Post>> { items ->
            postsAdapter.submitList(items)
        })
    }
}

