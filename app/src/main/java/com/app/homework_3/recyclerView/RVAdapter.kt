package com.app.homework_3.recyclerView

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.homework_3.Post
import com.app.homework_3.R
import com.app.homework_3.SharedViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.recycler_view_post_with_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_without_image.view.*

private const val TYPE_WITH_IMAGE = 0
private const val TYPE_WITHOUT_IMAGE = 1

class RVAdapter(private val sharedViewModel: SharedViewModel, posts: MutableList<Post>, private val clickListener: (ImageView?, Post) -> Unit) : RecyclerView.Adapter<RVAdapter.BaseViewHolder>(),
    ItemTouchHelperAdapter {

    private var mainList: MutableList<Post>
    private var differ = AsyncListDiffer(this,
        differCallback
    )
    private var posts: MutableList<Post>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    init {
        posts.sortBy { it.date }
        posts.reverse()
        mainList = mutableListOf<Post>().apply { posts.forEach { add(it) } }
        this.posts = posts
    }

    override fun getItemViewType(position: Int) =
        if (posts[position].image == null) TYPE_WITHOUT_IMAGE else TYPE_WITH_IMAGE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_WITH_IMAGE -> ViewHolderWithImage(
                clickListener,
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.recycler_view_post_with_image,
                        parent,
                        false
                    )
            )
            else -> ViewHolderWithoutImage(
                clickListener,
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

    class ViewHolderWithImage(private val clickListener: (ImageView?, Post) -> Unit, itemView: View) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            var view = itemView.postLayoutWithImage
            view.setContentPost(post.text)
            view.setIsLiked(post.isFavorite)
            view.setDatePost(post.date.toLong())
            Glide.with(itemView.context)
                .asBitmap()
                .load(post.image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        view.setImagePost(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            view.setOnClickListener { clickListener(view.getImagePost(), post) }
        }
    }

    class ViewHolderWithoutImage(private val clickListener: (ImageView?, Post) -> Unit, itemView: View) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            var view = itemView.postLayoutWithoutImage
            view.setContentPost(post.text)
            view.setIsLiked(post.isFavorite)
            view.setDatePost(post.date.toLong())
            view.setOnClickListener { clickListener(null, post) }
        }
    }

    override fun onItemDismiss(position: Int, direction: Int) {
        var tempPosts = posts.toMutableList()
        when (direction) {
            ItemTouchHelper.START -> {
                tempPosts.removeAt(position)
                posts = tempPosts
            }
            ItemTouchHelper.END -> {
                tempPosts[position].isFavorite = true
                posts = tempPosts
                notifyItemChanged(position)
            }
        }
        sharedViewModel.favorites.value = tempPosts.filter { it.isFavorite }
    }

    fun refresh() {
        posts = mainList.toMutableList()
    }

    fun update(posts: List<Post>) {
        this.posts = posts.toMutableList()
    }
}

private val differCallback = object : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldPost: Post, newPost: Post) = oldPost.id == newPost.id

    override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean {
        return oldPost.groupName == newPost.groupName &&
                oldPost.date == oldPost.date &&
                oldPost.text == newPost.text &&
                oldPost.isFavorite == newPost.isFavorite &&
                oldPost.image == newPost.image
    }
}