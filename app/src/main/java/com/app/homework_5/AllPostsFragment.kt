package com.app.homework_5

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
import com.app.homework_5.recyclerView.CustomItemDecorator
import com.app.homework_5.recyclerView.ItemTouchHelperAdapter
import com.app.homework_5.recyclerView.ItemTouchHelperCallback
import com.app.homework_5.recyclerView.PostsAdapter
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.posts_fragment.*
import kotlinx.android.synthetic.main.posts_fragment.view.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.posts_fragment, container, false)


    private fun getPosts(): List<Post> {
        val jsonString = requireActivity().assets.open("posts.json").bufferedReader().use {
            it.readText()
        }
        return Gson().fromJson(jsonString, Posts::class.java).posts
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val disposable = Observable.fromCallable {
            if (Random.nextInt(10) == 0)
                throw RuntimeException(":(")
            getPosts()
        }
            .subscribeOn(Schedulers.io())
            .delay(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { shimmerViewContainer.startShimmer() }
            .doFinally { hideShimmer() }
            .subscribeBy(
                onNext = { list ->
                    adapter.setData(list.toMutableList())
                    if (model.favorites.value == null || model.favorites.value?.let { it.size } == 0)
                        model.favorites.value = list.filter { it.isFavorite }
                },
                onError = {
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
                }
            )
        compositeDisposable.add(disposable)

        adapter = PostsAdapter(model) { text, image, post ->
            fragmentInteractor?.onOpenDetail(
                text,
                image,
                post.groupName,
                post.image,
                post.text
            )
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
            adapter.refresh()
            swipeContainer.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    private fun hideShimmer() {
        shimmerViewContainer.stopShimmer()
        shimmerViewContainer.visibility = View.GONE
    }
}