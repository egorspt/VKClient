package com.app.homework_3

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.homework_3.recyclerView.CustomItemDecorator
import com.app.homework_3.recyclerView.ItemTouchHelperAdapter
import com.app.homework_3.recyclerView.ItemTouchHelperCallback
import com.app.homework_3.recyclerView.PostsAdapter
import kotlinx.android.synthetic.main.posts_fragment.*
import kotlinx.android.synthetic.main.posts_fragment.view.*

class FavoritePostsFragment : Fragment() {

    private val model: SharedViewModel by activityViewModels()
    private var fragmentInteractor: FragmentInteractor? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor)
            fragmentInteractor = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PostsAdapter(model) { text, image, post ->
            fragmentInteractor?.onOpenDetail(text, image, post.groupName, post.image, post.text)
        }
        val recyclerView = view.recyclerView
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            this.adapter = adapter
            addItemDecoration(CustomItemDecorator(requireActivity()))
        }

        val callback =
            ItemTouchHelperCallback(adapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.divider_post_recycler_view
            )!!
        )

        view.swipeContainer.setOnRefreshListener {
            model.favorites.value?.let { adapter.update(it) }
            swipeContainer.isRefreshing = false
        }

        model.favorites.observe(viewLifecycleOwner, Observer<List<Post>> { posts ->
            hideShimmer()
            if (posts.isEmpty())
                errorText.text = getString(R.string.errorText)
            else
                adapter.update(posts)
        })
    }

    private fun hideShimmer() {
        shimmerViewContainer.stopShimmer()
        shimmerViewContainer.visibility = View.GONE
    }
}