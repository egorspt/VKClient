package com.app.tinkoff_fintech.paging

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.SharedViewModel
import com.app.tinkoff_fintech.recyclerView.ItemTouchHelperAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.post_layout_without_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_with_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_without_image.view.*

class PostsPagedListAdapter(
    private val sharedViewModel: SharedViewModel,
    private val changeLikes: (Int, Int, Int) -> Unit,
    private val clickListener: (TextView, ImageView?, Post) -> Unit,
    private val differ: DiffUtil.ItemCallback<Post>
) : PagedListAdapter<Post, PostsPagedListAdapter.BaseViewHolder>(differ),
    ItemTouchHelperAdapter {

    companion object {
        private const val TYPE_WITH_IMAGE = 0
        private const val TYPE_WITHOUT_IMAGE = 1
    }

    private var mainList = emptyList<Post>().toMutableList()

    override fun getItemViewType(position: Int) =
        if (getItem(position)!!.image == null) TYPE_WITHOUT_IMAGE else TYPE_WITH_IMAGE

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
            is ViewHolderWithImage -> holder.bind(getItem(position)!!)
            is ViewHolderWithoutImage -> holder.bind(getItem(position)!!)
        }
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderWithImage(
        private val clickListener: (TextView, ImageView?, Post) -> Unit,
        private val buttonLikeListener: (Int, Boolean) -> Unit,
        itemView: View
    ) : BaseViewHolder(itemView) {

        fun bind(post: Post) {
            val text = post.text ?: ""
            with(itemView.postLayoutWithImage) {
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
        val tempPosts = currentList!!
        var isLikes = 0
        val itemById = tempPosts.filter { it.id == id }[0]
        val positionItem = tempPosts.indexOf(itemById)
        when (isLiked) {
            true -> {
                itemById.likes.userLikes = 0
                itemById.likes.count -=  1
                isLikes = 0
            }
            false -> {
                itemById.likes.userLikes = 1
                itemById.likes.count +=  1
                isLikes = 1
            }
        }
        submitList(tempPosts)
        notifyItemChanged(positionItem)
        changeLikes(itemById.id, itemById.ownerId, isLikes)
        sharedViewModel.favorites.value = tempPosts.filter { it.likes.userLikes == 1 }
    }

    override fun onItemDismiss(position: Int, direction: Int) {
        val post = currentList!![position]!!
        when (direction) {
            ItemTouchHelper.START -> {
                changeLikes(post.id, true)
            }
            ItemTouchHelper.END -> {
                changeLikes(post.id, false)
            }
        }
    }
}