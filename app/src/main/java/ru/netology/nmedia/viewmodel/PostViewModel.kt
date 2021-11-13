package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryInMemory

private val empty = Post()

class PostViewModel : ViewModel() {
    private val repository = PostRepositoryInMemory()
    val edited = MutableLiveData(empty)
    val data = repository.get()

    fun like(id: Long) = repository.likeById(id)

    fun share(id: Long) = repository.shareById(id)

    fun remove(id: Long) = repository.removeById(id)

    fun editContent(text: String) {
        val formatted = text.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = formatted)
    }

    fun add() {
        edited.value?.let {
            repository.add(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

}