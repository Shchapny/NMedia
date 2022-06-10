package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class Post(
    override val id: Long = 0,
    val authorId: Long = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val published: Long = Date().time,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val likes: Long = 0,
    val share: Long = 0,
    val video: String? = null,
    val newPost: Boolean = false,
    var attachment: Attachment? = null,
    @SerializedName("ownerByMe")
    val ownedById: Boolean = false
): FeedItem
