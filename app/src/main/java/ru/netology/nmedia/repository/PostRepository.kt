package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
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
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getNewPosts()

    val data: Flow<List<Post>>
}