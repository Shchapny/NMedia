package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {
    private val repository = PostRepositoryInMemory()

    fun like(id: Long) {
        repository.likeById(id)
    }

    val data = repository.get()

    fun share(id: Long) {
        repository.shareById(id)
    }
}