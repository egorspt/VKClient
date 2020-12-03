package com.app.tinkoff_fintech.ui.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.recyclerView.*
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.presenters.FavoritesPresenter
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.posts_fragment.*
import javax.inject.Inject

class FavoritePostsFragment : Fragment(), FavoritesContractInterface.View {

    @Inject
    lateinit var presenter: FavoritesPresenter
    @Inject
    lateinit var postsAdapter: PostsAdapter
    private val model: SharedViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        (activity?.application as App).addFavoritesPostsComponent(this)
        (activity?.application as App).favoritesComponent?.inject(this)
        super.onAttach(context)
    }

    override fun init() {
        val callback = ItemTouchHelperCallback(postsAdapter as SwipeListener)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.divider_post_space
        )?.let {
            dividerItemDecoration.setDrawable(it)
        }

        model.favorites.observe(viewLifecycleOwner, Observer<List<Post>> { posts ->
            hideShimmer()
            if (posts.isEmpty())
                textError.text = getString(R.string.errorText)
            else
                postsAdapter.setData(posts.toMutableList())
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(this)
        val dividerItemDecoration = DividerItemDecoration(activity, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_post_space,
                null
            )!!
        )
        with (postsAdapter) {
            sharedViewModel = model
            postClickListener = { startDetailActivity(it) }
            changeLikes = { itemId, ownerId, isLikes ->
                changeLike(itemId, ownerId, isLikes)
            }
        }
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            this.adapter = postsAdapter
            addItemDecoration(dividerItemDecoration)
        }

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = false
        }
    }

    private fun hideShimmer() {
        shimmerViewContainer.stopShimmer()
        shimmerViewContainer.visibility = View.GONE
    }

    private fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        presenter.changeLike(postId, postOwnerId, isLikes)
    }

    private fun startDetailActivity(id: Int) {
        requireActivity().startActivity(Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, id)
        })
    }

    private fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, post: Post) {
        val arrayPairs = if (sharedImageView == null)
            arrayOf(Pair.create(sharedTextView as View, getString(R.string.transitionNameText)))
        else arrayOf(
            Pair.create(sharedImageView as View, getString(R.string.transitionNameImage)),
            Pair.create(sharedTextView as View, getString(R.string.transitionNameText))
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), *arrayPairs)
        activity?.startActivity(Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.ARG_POST_ID, post.id)
        }, options.toBundle())
    }
}