package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.R.id
import ru.netology.nmedia.databinding.CardPostFragmentBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.DisplayCount
import ru.netology.nmedia.util.loadImage

val displayCount = DisplayCount()

interface PostActionListener {
    fun edit(post: Post)
    fun remove(post: Post)
    fun like(post: Post)
    fun share(post: Post)
    fun playVideo(post: Post)
    fun showPost(post: Post)
}

class PostAdapter(
    private val listener: PostActionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            CardPostFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostFragmentBinding,
    private val listener: PostActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likes.text = displayCount.display(post.likes)
            share.text = displayCount.display(post.share)
            likes.isChecked = (post.likedByMe)

            likes.setOnClickListener {
                listener.like(post)
            }

            share.setOnClickListener {
                listener.share(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.posts_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            id.remove -> {
                                listener.remove(post)
                                true
                            }
                            id.edit -> {
                                listener.edit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            video.setOnClickListener {
                listener.playVideo(post)
            }

            play.setOnClickListener {
                listener.playVideo(post)
            }

            showPost.setOnClickListener {
                listener.showPost(post)
            }
            if (post.video != null) groupVideo.visibility =
                View.VISIBLE else groupVideo.visibility = View.GONE

            val url = "http://10.0.2.2:9999"

            avatar.loadImage(url, "avatars", post.authorAvatar)
            if (post.attachment != null) video.loadImage(
                url,
                "images",
                post.attachment?.url
            )
            else video.visibility = View.GONE
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
