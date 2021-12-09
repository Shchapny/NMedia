package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val views: Long,
    val likes: Long,
    val share: Long,
    val video: String?
) {

    companion object {
        fun fromDto(dto: Post): PostEntity = with(dto) {
            PostEntity(
                id = id,
                author = author,
                content = content,
                published = published,
                likedByMe = likedByMe,
                views = views,
                likes = likes,
                share = share,
                video = video
            )
        }
    }

    fun toDto(): Post = with(this) {
        Post(
            id = id,
            author = author,
            content = content,
            published = published,
            likedByMe = likedByMe,
            views = views,
            likes = likes,
            share = share,
            video = video
        )
    }
}

private val formattedDate = SimpleDateFormat.getDateTimeInstance()!!
    .format(Calendar.getInstance().time!!)!!