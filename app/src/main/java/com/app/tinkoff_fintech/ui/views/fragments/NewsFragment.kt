package com.app.tinkoff_fintech.ui.views.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.paging.news.NewsViewModel
import com.app.tinkoff_fintech.recycler.adapters.NewsAdapter
import com.app.tinkoff_fintech.recycler.decorations.PostDecorator
import com.app.tinkoff_fintech.recycler.touchHelpers.ItemTouchHelperCallback
import com.app.tinkoff_fintech.recycler.touchHelpers.SwipeListener
import com.app.tinkoff_fintech.ui.contracts.NewsContractInterface
import com.app.tinkoff_fintech.ui.presenters.NewsPresenter
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.utils.State
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.news_fragment.*
import javax.inject.Inject

class NewsFragment : Fragment(), NewsContractInterface.View {

    @Inject
    lateinit var presenter: NewsPresenter

    @Inject
    lateinit var newsAdapter: NewsAdapter

    @Inject
    lateinit var preferencesService: PreferencesService

    @Inject
    lateinit var postDecorator: PostDecorator

    private val viewModel: NewsViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        (activity?.applicationContext as App).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View = inflater.inflate(R.layout.news_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun updateNews() {
        viewModel.invalidate()
    }

    override fun init() {
        if (!viewModel.isInitialized()) {
            (activity?.application as App).appComponent.inject(viewModel)
            viewModel.init()
        }
        initState()
        initAdapter()
        buttonLogin.setOnClickListener {
            activity?.viewModelStore?.clear()
            vkLogin()
        }
    }

    override fun updateLikes(postId: Int, countLikes: Int, isLiked: Boolean) {
        newsAdapter.getItemPosition(postId)?.let {
            val post = newsAdapter.getItem(it)
            post?.isLiked = isLiked
            post?.countLikes = countLikes
            newsAdapter.notifyItemChanged(it, post)
        }
    }

    private fun initAdapter() {
        with(newsAdapter) {
            changeLikesListener =
                { itemId, ownerId, isLikes -> changeLike(itemId, ownerId, isLikes) }
            postClickListener = { id -> startDetailActivity(id) }
            retry = { viewModel.retry() }
        }
        fab.setOnClickListener { recyclerView.scrollToPosition(0); fab.visibility = View.GONE }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = newsAdapter
            addItemDecoration(postDecorator)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy >= 0) {
                        fab.visibility = View.GONE
                    }
                    if (dy < -50) {
                        fab.visibility = View.VISIBLE
                    }
                }
            })
        }

        val callback = ItemTouchHelperCallback(1, newsAdapter as SwipeListener)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        swipeContainer.setOnRefreshListener {
            presenter.refreshNews()
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.newsList.observe(requireActivity(), Observer<PagedList<Post>> { news ->
            newsAdapter.submitList(news)
        })
        presenter.getFavorites().observe(requireActivity(), Observer { favorites ->
            favorites.forEach {
                newsAdapter.getItemPosition(it.id)
                    ?.let { it1 -> newsAdapter.notifyItemChanged(it1, it) }
            }
        })
        presenter.getNotFavorites().observe(requireActivity(), Observer { favorites ->
            favorites.forEach {
                newsAdapter.getItemPosition(it.id)
                    ?.let { it1 -> newsAdapter.notifyItemChanged(it1, it) }
            }
        })
    }

    private fun initState() {
        viewModel.getState().observe(requireActivity(), Observer { state ->
            buttonLogin.visibility =
                if (viewModel.listIsEmpty() && state == State.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty() || state == State.ERROR) {
                hideShimmer()
                newsAdapter.setStateAdapter(state ?: State.DONE)
            }
        })
    }

    override fun hideShimmer() {
        requireActivity().runOnUiThread {
            swipeContainer.isRefreshing = false
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
        }
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLiked: Boolean) {
        presenter.changeLike(postId, postOwnerId, isLiked)
    }

    private fun startDetailActivity(id: Int) {
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, id)
        }
        val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        startActivity(intent, bundle)
    }

    private fun vkLogin() {
        VK.login(
            requireActivity(),
            arrayListOf(VKScope.WALL, VKScope.FRIENDS, VKScope.PHOTOS, VKScope.DOCS)
        )
    }
}

