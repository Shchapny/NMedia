package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType


data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType
) {
    fun toDto() = Attachment(url = url, type = type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let { attachment ->
            AttachmentEmbeddable(attachment.url, attachment.type)
        }
    }
}

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val views: Long,
    val likes: Long,
    val share: Long,
    val video: String?,
    val newPost: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?
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
                video = video,
                newPost = newPost,
                authorAvatar = authorAvatar,
                attachment = AttachmentEmbeddable.fromDto(dto = dto.attachment)
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
            video = video,
            newPost = newPost,
            authorAvatar = authorAvatar,
            attachment = attachment?.toDto()
        )
    }
}

fun List<PostEntity>.toDto() = map { it.toDto() }

fun List<Post>.toEntity() = map { PostEntity.fromDto(it) }