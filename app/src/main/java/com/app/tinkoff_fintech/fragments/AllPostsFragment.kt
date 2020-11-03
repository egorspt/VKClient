package com.app.tinkoff_fintech.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import com.app.tinkoff_fintech.Constants.Companion.LAST_REFRESH_NEWSFEED
import com.app.tinkoff_fintech.Constants.Companion.NEED_UPDATE_NEWSFEED
import com.app.tinkoff_fintech.FragmentInteractor
import com.app.tinkoff_fintech.PreferencesService
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.SharedViewModel
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.paging.PostViewModel
import com.app.tinkoff_fintech.paging.PostsPagedListAdapter
import com.app.tinkoff_fintech.recyclerView.CustomItemDecorator
import com.app.tinkoff_fintech.recyclerView.DifferCallback
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperAdapter
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.posts_fragment.*
import kotlinx.android.synthetic.main.posts_fragment.view.*
import java.util.*
import java.util.concurrent.TimeUnit


class AllPostsFragment : Fragment() {

    private lateinit var preferences: PreferencesService
    private lateinit var databasePost: PostDao
    private val model: SharedViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private var fragmentInteractor: FragmentInteractor? = null
    private lateinit var adapterPaging: PostsPagedListAdapter
    private var firstOnResume = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor) {
            fragmentInteractor = context
            preferences = PreferencesService(context)
            preferences.put(NEED_UPDATE_NEWSFEED, true)
            databasePost = DatabaseService(context).defaultDatabase().postDao()
            adapterPaging = PostsPagedListAdapter(model,
                { itemId, ownerId, isLikes ->
                    fragmentInteractor?.changeLikes(itemId, ownerId, isLikes)
                },
                { text, image, post ->
                    fragmentInteractor?.onOpenDetail(text, image, post)
                }, DifferCallback()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (firstOnResume)
            firstOpen()
        else
            checkRelevanceNewsfeed()
    }

    private fun firstOpen() {
        firstOnResume = false
        preferences.put(NEED_UPDATE_NEWSFEED, true)
        databasePost.deleteAll()
            .subscribeOn(Schedulers.io())
            .subscribe()
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
            addItemDecoration(CustomItemDecorator(requireActivity()))
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
            ItemTouchHelperCallback(adapterPaging as ItemTouchHelperAdapter)
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
            preferences.put(LAST_REFRESH_NEWSFEED, Calendar.getInstance().time.time)
            updateNewsfeed()
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
            addToDatabase(list)
        })
    }

    private fun hideShimmer() {
        shimmerViewContainer.stopShimmer()
        shimmerViewContainer.visibility = View.GONE
    }

    //кэширование данных по времени. каждый час обновляется лента
    private fun checkRelevanceNewsfeed() {
        val lastRefreshNewsfeedTime = preferences.getLong(LAST_REFRESH_NEWSFEED)
        val currentTime = Calendar.getInstance().time.time
        if (lastRefreshNewsfeedTime == 0L) preferences.put(LAST_REFRESH_NEWSFEED, currentTime)
        if (currentTime - lastRefreshNewsfeedTime > TimeUnit.HOURS.toMillis(1)) {
            preferences.put(NEED_UPDATE_NEWSFEED, true)
            updateNewsfeed()
        } else
            preferences.put(NEED_UPDATE_NEWSFEED, false)
    }

    @SuppressLint("CheckResult")
    private fun addToDatabase(list: List<Post>) {
        databasePost.getAll()
            .subscribeOn(Schedulers.single())
            .doFinally {
                requireActivity().runOnUiThread {
                    hideShimmer()
                    swipeContainer.isRefreshing = false
                }
            }
            .subscribe { databaseList ->
                val tempList = mutableListOf<Post>()
                list.forEach { post ->
                    if (databaseList.filter { it.id == post.id }.isEmpty())
                        tempList.add(post)
                }
                tempList.addAll(databaseList)
                databasePost.deleteAll()
                    .subscribeBy(
                        onError = { requireActivity().runOnUiThread { showLocalError(it.message) } },
                        onComplete = {
                            requireActivity().runOnUiThread { model.favorites.value = tempList.filter { it.likes.userLikes == 1 } }
                            databasePost.insertAll(tempList)
                                .subscribeBy(
                                    onError = {
                                        showLocalError(it.message)
                                    }
                                )
                        }
                    )

            }
    }

    private fun updateNewsfeed() {
        errorText.visibility = View.GONE
        preferences.put(NEED_UPDATE_NEWSFEED, true)
        databasePost.deleteAll()
            .subscribeOn(Schedulers.io())
            .subscribe()
        postViewModel.invalidate()
    }

    private fun showError(message: String) {
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

    private fun showLocalError(message: String?) {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.dialogErrorTitle))
            .setMessage(message)
            .show()
    }
}

