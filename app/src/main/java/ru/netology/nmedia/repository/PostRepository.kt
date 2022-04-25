package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {

    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Long
    suspend fun getAll()
    suspend fun getById(id: Long): Post
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getNewPosts()
    suspend fun uploadMedia(upload: MediaUpload): Media
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun authentication(login: String, password: String)
    suspend fun registration(login: String, password: String, name: String)
    suspend fun registerWithPhoto(login: String, password: String, name: String, mediaUpload: MediaUpload)

    val data: Flow<List<Post>>
}