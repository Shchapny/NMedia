package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun shareById(id: Long, callback: Callback<Post>)
    fun likeById(id: Long, callback: Callback<Post>)
    fun removeById(id: Long, callback: Callback<Unit>)
//    fun get(): List<Post>
    fun save(post: Post, callback: Callback<Post>)
    fun video()
    fun unlikeById(id: Long, callback: Callback<Post>)

    fun getAllAsync(callback: Callback<List<Post>>)

    interface Callback<T> {
        fun onSuccess(post: T) {}
        fun onError(e: Exception) {}
    }
}