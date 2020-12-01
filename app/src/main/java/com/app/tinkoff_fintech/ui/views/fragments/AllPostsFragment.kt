package com.app.tinkoff_fintech.ui.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.utils.Constants.Companion.NEED_UPDATE_NEWSFEED
import com.app.tinkoff_fintech.mainActivity.FragmentInteractor
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.DetailActivity
import com.app.tinkoff_fintech.ui.views.fragments.mvp.IPostsView
import com.app.tinkoff_fintech.ui.views.fragments.mvp.PostsPresenter
import com.app.tinkoff_fintech.paging.news.PostViewModel
import com.app.tinkoff_fintech.paging.news.PostsPagedListAdapter
import com.app.tinkoff_fintech.recyclerView.*
import kotlinx.android.synthetic.main.posts_fragment.*
import kotlinx.android.synthetic.main.posts_fragment.view.*


class AllPostsFragment : Fragment(), IPostsView {

    private lateinit var preferences: PreferencesService
    private lateinit var adapterPaging: PostsPagedListAdapter
    private lateinit var presenter: PostsPresenter
    private val model: SharedViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private var fragmentInteractor: FragmentInteractor? = null
    private var firstOnResume = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor) {
            fragmentInteractor = context
            preferences =
                PreferencesService(context)
            preferences.put(NEED_UPDATE_NEWSFEED, true)
            adapterPaging =
                PostsPagedListAdapter(
                    model,
                    { itemId, ownerId, isLikes ->
                        fragmentInteractor?.changeLikes(itemId, ownerId, isLikes)
                    },
                    { text, image, post ->
                        fragmentInteractor?.onOpenDetail(text, image, post)
                    }, DifferCallback()
                )
            adapterPaging.clickImage = { url -> clickImage(url) }
            presenter = PostsPresenter(this)
        }
    }

    private fun clickImage(url: String) {
        requireActivity().startActivity(Intent(activity, ImageActivity::class.java).apply {
            putExtra(DetailActivity.ARG_URL_IMAGE, url)
        })
    }

    override fun onResume() {
        super.onResume()
        if (firstOnResume)
            firstOpen()
        else
            presenter.checkRelevanceNewsfeed(preferences)
    }

    private fun firstOpen() {
        firstOnResume = false
        preferences.put(NEED_UPDATE_NEWSFEED, true)
        presenter.deleteAllFromDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()

        fab.setOnClickListener { recyclerView.scrollToPosition(0); fab.visibility = View.GONE }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = adapterPaging
            addItemDecoration(PostDecorator())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy >= 0) {
                        view.fab?.visibility = View.GONE
                    } else {
                        view.fab?.visibility = View.VISIBLE
                    }
                }
            })
        }

        val callback =
            ItemTouchHelperCallback(adapterPaging as SwipeListener)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.divider_post_space
            )!!
        )

        view.swipeContainer.setOnRefreshListener {
            presenter.refreshNewsfeed(preferences)
        }
    }

    private fun initListeners() {
        postViewModel.postPagedList.observe(requireActivity(), Observer<PagedList<Post>> { items ->
            adapterPaging.submitList(items)
        })
        postViewModel.errorListener.observe(requireActivity(), Observer<String> { message ->
            showError(message)
        })
        postViewModel.postDatabaseList.observe(requireActivity(), Observer<List<Post>> { list ->
            presenter.updateDatabase(requireContext(), list)
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
        with(errorText) {
            visibility = View.VISIBLE
            text = getString(R.string.errorText, message)
        }
        hideShimmer()
        swipeContainer.isRefreshing = false
    }

    override fun updateNewsfeed() {
        errorText.visibility = View.GONE
        postViewModel.invalidate()
        presenter.deleteAllFromDatabase(requireContext())
    }

    override fun hideShimmer() {
        requireActivity().runOnUiThread {
            swipeContainer.isRefreshing = false
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
        }
    }

    override fun showDatabaseError(message: String?) {
        requireActivity().runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle(getString(R.string.dialogErrorTitle))
                .setMessage(message)
                .show()
        }
    }

    override fun updateFavorites(list: List<Post>) {
        requireActivity().runOnUiThread {
            model.favorites.value = list
        }
    }
}

