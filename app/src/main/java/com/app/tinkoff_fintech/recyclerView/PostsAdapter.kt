package com.app.tinkoff_fintech.recyclerView

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import javax.inject.Inject

typealias postClickListener = (Int) -> Unit
typealias changeLikes = (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit

class PostsAdapter @Inject constructor() : RecyclerView.Adapter<NewsPostViewHolder>(),
    SwipeListener {

    lateinit var postClickListener: postClickListener
    lateinit var changeLikes: changeLikes
    lateinit var sharedViewModel: SharedViewModel

    private val differCallback = object : DiffUtil.ItemCallback<Post>() {

        override fun areItemsTheSame(oldPost: Post, newPost: Post) = oldPost.id == newPost.id

        override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean {
            return oldPost.ownerName == newPost.ownerName &&
                    oldPost.date == oldPost.date &&
                    oldPost.text == newPost.text &&
                    oldPost.likes.userLikes == newPost.likes.userLikes &&
                    oldPost.image == newPost.image
        }
    }

    private var mainList = emptyList<Post>().toMutableList()
    private var differ = AsyncListDiffer(
        this,
        differCallback
    )
    private var posts: MutableList<Post>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostViewHolder {
        return NewsPostViewHolder.create(parent, postClickListener, changeLikes)
    }

    override fun onBindViewHolder(holder: NewsPostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = differ.currentList.size

    private fun changeLike(position: Int, isLiked: Boolean) {
        val post = posts[position]
        when (isLiked) {
            true -> {
                post.likes.userLikes = 0
                post.likes.count -=  1
                notifyItemRemoved(position)
                sharedViewModel.favorites.value = posts.filter { it.likes.userLikes == 1 }
                changeLikes(post.id, post.ownerId, isLiked)
            }
            false -> return
        }
    }

    override fun onSwipe(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                changeLike(position,true)
            }
            ItemTouchHelper.END -> {
                changeLike(position, false)
            }
        }
    }

    fun setData(posts: List<Post>) {
        val tempPosts = posts.toMutableList()
        tempPosts.sortBy { it.date }
        tempPosts.reverse()
        mainList = mutableListOf<Post>().apply { tempPosts.forEach { add(it) } }
        this.posts = tempPosts
    }
}