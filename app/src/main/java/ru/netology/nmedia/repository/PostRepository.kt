package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    suspend fun shareById(id: Long): Post
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Long
    suspend fun video()
    suspend fun unlikeById(id: Long)
    suspend fun getAll()

    val data: LiveData<List<Post>>
}