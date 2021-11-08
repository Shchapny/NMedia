package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun shareById(id: Long)
    fun likeById(id: Long)
    fun get(): LiveData<List<Post>>
}