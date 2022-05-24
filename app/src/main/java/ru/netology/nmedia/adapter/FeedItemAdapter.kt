package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.R.id
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.util.DisplayCount
import ru.netology.nmedia.util.loadImage

interface PostActionListener {
    fun edit(post: Post)
    fun remove(post: Post)
    fun like(post: Post)
    fun share(post: Post)
    fun video(post: Post)
    fun showPost(post: Post)
    fun showImage(post: Post)
}

class FeedItemAdapter(
    private val listener: PostActionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("Unknown view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, listener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            else -> error("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as AdViewHolder).bind(item)
            is Post -> (holder as PostViewHolder).bind(item)
            null -> error("Unknown view type $item")
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likes.text = DisplayCount.display(post.likes)
            share.text = DisplayCount.display(post.share)
            likes.isChecked = (post.likedByMe)

            likes.setOnClickListener {
                listener.like(post)
            }

            share.setOnClickListener {
                listener.share(post)
            }

            menu.visibility = if (post.ownedById) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.posts_menu)
                    menu.setGroupVisible(id.owned, post.ownedById)
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
                listener.video(post)
            }

            play.setOnClickListener {
                listener.video(post)
            }

            showPost.setOnClickListener {
                listener.showPost(post)
            }

            imagePost.setOnClickListener {
                listener.showImage(post)
            }

            if (post.video != null) groupVideo.visibility =
                View.VISIBLE else groupVideo.visibility = View.GONE

            avatar.loadImage(BuildConfig.BASE_URL, "avatars", post.authorAvatar)

            if (post.attachment != null && post.attachment?.type == AttachmentType.IMAGE) {
                imagePost.visibility = View.VISIBLE
                imagePost.loadImage(BuildConfig.BASE_URL, "media", post.attachment?.url)
            } else imagePost.visibility = View.GONE
        }
    }
}

class AdViewHolder(private val binding: CardAdBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.imageAd.loadImage(BuildConfig.BASE_URL, "media", ad.image)
    }
}

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
