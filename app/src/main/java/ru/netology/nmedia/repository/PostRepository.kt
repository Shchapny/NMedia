package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun shareById(id: Long, callback: GetIdCallback)
    fun likeById(id: Long, callback: GetIdCallback)
    fun removeById(id: Long, callback: GetIdCallback)
    fun get(): List<Post>
    fun add(post: Post, callback: GetPostCallback)
    fun video()
    fun unlikeById(id: Long, callback: GetIdCallback)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface GetIdCallback {
        fun onSuccess(id: Long) {}
        fun onError(e: Exception) {}
    }

    interface GetPostCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}