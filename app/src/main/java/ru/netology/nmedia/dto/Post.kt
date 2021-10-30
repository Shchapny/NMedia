package ru.netology.nmedia.dto

data class Post(
    val id: Long = 0,
    val author: String = "",
    val content: String = "",
    val published: String = "",
    var likedByMe: Boolean = false,
    val views: Long = 0,
    var likes: Long = 0,
    var share: Long = 0
)
