package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)

data class Post(
    val id: Long = 0,
    val author: String = "Нетология. Университет интернет-профессий будущего",
    val authorAvatar: String = "",
    val content: String = "",
    val published: String = "",
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val likes: Long = 0,
    val share: Long = 0,
    val video: String? = null,
    val newPost: Boolean = false,
    var attachment: Attachment? = null
)
