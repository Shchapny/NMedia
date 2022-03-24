package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun shareById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Long
    suspend fun video()
//    suspend fun unlikeById(id: Long)
    suspend fun getAll()
    suspend fun getById(id: Long): Post

    val data: LiveData<List<Post>>
}