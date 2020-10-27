package com.app.tinkoff_fintech.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.*
import com.app.tinkoff_fintech.recyclerView.CustomItemDecorator
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperAdapter
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperCallback
import com.app.tinkoff_fintech.recyclerView.PostsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.posts_fragment.*
import kotlinx.android.synthetic.main.posts_fragment.view.*
import kotlin.math.abs


class AllPostsFragment : Fragment() {

    private val model: SharedViewModel by activityViewModels()
    private var fragmentInteractor: FragmentInteractor? = null
    private val compositeDisposable = CompositeDisposable()
    lateinit var adapter: PostsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor)
            fragmentInteractor = context
    }

    override fun onResume() {
        super.onResume()
        getVkNewsfeed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostsAdapter(model, { text, image, post ->
            fragmentInteractor?.onOpenDetail(text, image, post)
        }, { itemId, ownerId, isLikes ->
            fragmentInteractor?.changeLikes(itemId, ownerId, isLikes)
        })
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
            getVkNewsfeed()
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun hideShimmer() {
        shimmerViewContainer.stopShimmer()
        shimmerViewContainer.visibility = View.GONE
    }

    private fun getVkNewsfeed() {
        val disposable = NetworkService().create().getNewsfeed()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                hideShimmer()
                swipeContainer.isRefreshing = false
            }
            .subscribeBy(
                {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialogErrorTitle))
                        .setMessage(it.message)
                        .setPositiveButton(getString(R.string.dialogPositiveButtonText)) { dialog, _ ->
                            dialog.cancel()
                        }.show()
                    with(errorText) {
                        visibility = View.VISIBLE
                        text = getString(R.string.errorText, it.message)
                    }
                },
                { result ->
                    val list: MutableList<Post> = mutableListOf()
                    result.response.items.forEach { item ->
                        val ownerImage = if (item.source_id > 0)
                            result.response.profiles.filter { it.id == abs(item.source_id) }[0].photo_100
                        else result.response.groups.filter { it.id == abs(item.source_id) }[0].photo_200
                        val ownerName = if (item.source_id > 0)
                            result.response.profiles.filter { it.id == abs(item.source_id) }[0].first_name + " " +
                                    result.response.profiles.filter { it.id == abs(item.source_id) }[0].last_name
                        else result.response.groups.filter { it.id == abs(item.source_id) }[0].name
                        list.add(
                            Post(
                                item.post_id,
                                item.source_id,
                                ownerImage,
                                ownerName,
                                item.date.toLong(),
                                item.text,
                                item.attachments?.get(0)?.photo?.sizes?.last()?.url,
                                Likes(item.likes.count, item.likes.user_likes),
                                Comments(item.comments.count),
                                null
                            )
                        )
                    }

                    adapter.setData(list)
                    model.favorites.value = list.filter { it.likes.userLikes == 1 }
                })
        compositeDisposable.add(disposable)
    }
}