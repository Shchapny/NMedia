package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepositoryInMemory

class PostViewModel: ViewModel() {
    private val repository = PostRepositoryInMemory()

    fun like() {
        repository.like()
    }

    fun get() = repository.get()

    fun share() {
        repository.share()
    }
}