package com.app.homework_3

import android.app.Activity
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
import com.app.homework_3.recyclerView.RVAdapter
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
        val adapter = RVAdapter(
            model,
            emptyList<Post>().toMutableList()
        ) { image, post ->
            fragmentInteractor?.onOpenDetail(image, post.groupName, post.image, post.text)
        }
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = adapter

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
        recyclerView.addItemDecoration(
            CustomItemDecorator(
                requireActivity()
            )
        )

        view.swipeContainer.setOnRefreshListener {
            model.favorites.value?.let { adapter.update(it) }
            swipeContainer.isRefreshing = false
        }

        model.favorites.observe(viewLifecycleOwner, Observer<List<Post>> { posts ->
            adapter.update(posts)
        })
    }
}