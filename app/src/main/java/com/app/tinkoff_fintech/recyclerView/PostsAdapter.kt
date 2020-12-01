package com.app.tinkoff_fintech.recyclerView

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.post_layout_without_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_with_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_without_image.view.*

class PostsAdapter(
    private val sharedViewModel: SharedViewModel,
    private val clickListener: (TextView, ImageView?, Post) -> Unit,
    private val changeLikes: (Int, Int, Int) -> Unit
) : RecyclerView.Adapter<PostsAdapter.BaseViewHolder>(),
    SwipeListener {

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

    companion object {
        private const val TYPE_WITH_IMAGE = 0
        private const val TYPE_WITHOUT_IMAGE = 1
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

    override fun getItemViewType(position: Int) =
        if (posts[position].image == null) TYPE_WITHOUT_IMAGE else TYPE_WITH_IMAGE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_WITH_IMAGE -> ViewHolderWithImage(
                clickListener,
                { id, isLiked -> changeLikes(id, isLiked) },
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.recycler_view_post_with_image,
                        parent,
                        false
                    )
            )
            else -> ViewHolderWithoutImage(
                clickListener,
                { id, isLiked -> changeLikes(id, isLiked) },
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.recycler_view_post_without_image,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is ViewHolderWithImage -> holder.bind(posts[position])
            is ViewHolderWithoutImage -> holder.bind(posts[position])
        }
    }

    override fun getItemCount() = differ.currentList.size

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderWithImage(
        private val clickListener: (TextView, ImageView?, Post) -> Unit,
        private val buttonLikeListener: (Int, Boolean) -> Unit,
        itemView: View
    ) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            val text = post.text ?: ""
            with(itemView.postLayout) {
                setOwnerImage(post.ownerImage)
                setOwnerName(post.ownerName)
                setContentPost(text)
                setIsLiked(post.likes.userLikes == 1)
                setDatePost(post.date)
                setCountLikes(post.likes.count)
                setCountComments(post.comments.count)
                buttonLike.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
                countLikes.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(post.image)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            setImagePost(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
                setOnClickListener {
                    clickListener(
                        getContentPost(),
                        getImagePost(),
                        post
                    )
                }
            }
        }
    }

    class ViewHolderWithoutImage(
        private val clickListener: (TextView, ImageView?, Post) -> Unit,
        private val buttonLikeListener: (Int, Boolean) -> Unit,
        itemView: View
    ) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            val text = post.text ?: ""
            with(itemView.postLayoutWithoutImage) {
                setOwnerImage(post.ownerImage)
                setOwnerName(post.ownerName)
                setContentPost(text)
                setIsLiked(post.likes.userLikes == 1)
                setDatePost(post.date)
                setCountLikes(post.likes.count)
                setCountComments(post.comments.count)
                setOnClickListener { clickListener(getContentPost(), null, post) }
                buttonLike.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
                countLikes.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
            }
        }
    }

    private fun changeLikes(id: Int, isLiked: Boolean) {
        val tempPosts = posts.toMutableList()
        var isLikes = 0
        val itemById = tempPosts.filter { it.id == id }[0]
        val positionItem = tempPosts.indexOf(itemById)
        when (isLiked) {
            true -> {
                itemById.likes.userLikes = 0
                itemById.likes.count -=  1
                posts = tempPosts
                isLikes = 0
            }
            false -> {
                itemById.likes.userLikes = 1
                itemById.likes.count +=  1
                posts = tempPosts
                isLikes = 1
            }
        }
        notifyItemChanged(positionItem)
        changeLikes(posts[positionItem].id, posts[positionItem].ownerId, isLikes)
        sharedViewModel.favorites.value = tempPosts.filter { it.likes.userLikes == 1 }
    }

    override fun onSwipe(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                changeLikes(posts[position].id, true)
            }
            ItemTouchHelper.END -> {
                changeLikes(posts[position].id, false)
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