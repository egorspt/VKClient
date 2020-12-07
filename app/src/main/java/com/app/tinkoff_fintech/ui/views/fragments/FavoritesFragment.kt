package com.app.tinkoff_fintech.ui.views.fragments

import android.app.ActivityOptions
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.touchHelpers.ItemTouchHelperCallback
import com.app.tinkoff_fintech.recycler.adapters.FavoritesAdapter
import com.app.tinkoff_fintech.recycler.decorations.PostDecorator
import com.app.tinkoff_fintech.recycler.touchHelpers.SwipeListener
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.presenters.FavoritesPresenter
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import kotlinx.android.synthetic.main.news_fragment.*
import javax.inject.Inject

class FavoritesFragment : Fragment(), FavoritesContractInterface.View {

    @Inject
    lateinit var presenter: FavoritesPresenter

    @Inject
    lateinit var favoritesAdapter: FavoritesAdapter

    @Inject
    lateinit var postDecorator: PostDecorator

    override fun onAttach(context: Context) {
        (activity?.application as App).addFavoritesPostsComponent(this)
        (activity?.application as App).favoritesComponent?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View = inflater.inflate(R.layout.favorites_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
    }

    override fun init() {
        val callback =
            ItemTouchHelperCallback(1, favoritesAdapter as SwipeListener)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        with(favoritesAdapter) {
            postClickListener = { startDetailActivity(it) }
            changeLikesListener = { itemId, ownerId, isLikes ->
                changeLike(itemId, ownerId, isLikes)
            }
        }

        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            this.adapter = favoritesAdapter
            addItemDecoration(postDecorator)
        }

        initListeners()
    }

    private fun initListeners() {
        presenter.getFavorites().observe(activity as LifecycleOwner, Observer<List<Post>> { posts ->
            favoritesAdapter.setData(posts.toMutableList())
        })
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        presenter.changeLike(postId, postOwnerId, isLikes)
    }

    private fun startDetailActivity(id: Int) {
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, id)
        }
        val bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
        startActivity(intent, bundle)
    }
}