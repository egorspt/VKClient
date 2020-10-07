package com.app.homework_3

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.recycler_view_post_with_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_without_image.view.*

private const val TYPE_WITH_IMAGE = 0
private const val TYPE_WITHOUT_IMAGE = 1

class RVAdapter(posts: MutableList<Post>) : RecyclerView.Adapter<RVAdapter.BaseViewHolder>(),
    ItemTouchHelperAdapter {

    private var mainList: MutableList<Post>
    private var differ = AsyncListDiffer(this, differCallback)
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
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_post_with_image, parent, false)
            )
            else -> ViewHolderWithoutImage(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_post_without_image, parent, false)
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

    class ViewHolderWithImage(itemView: View) : BaseViewHolder(itemView) {

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
        }
    }

    class ViewHolderWithoutImage(itemView: View) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            var view = itemView.postLayoutWithoutImage
            view.setContentPost(post.text)
            view.setIsLiked(post.isFavorite)
            view.setDatePost(post.date.toLong())
        }
    }

    override fun onItemDismiss(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                var tempPosts = posts.toMutableList()
                tempPosts.removeAt(position)
                posts = tempPosts
            }
            ItemTouchHelper.END -> {
                var tempPosts = posts.toMutableList()
                tempPosts[position].isFavorite = true
                posts = tempPosts
                notifyItemChanged(position)
            }
        }
    }

    fun refresh() {
        posts = mainList.toMutableList()
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