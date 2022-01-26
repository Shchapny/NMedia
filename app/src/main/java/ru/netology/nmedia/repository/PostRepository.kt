package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun shareById(id: Long)
    fun likeById(id: Long)
    fun removeById(id: Long)
    fun get(): List<Post>
    fun add(post: Post)
    fun video()
    fun unlikeById(id: Long)
}