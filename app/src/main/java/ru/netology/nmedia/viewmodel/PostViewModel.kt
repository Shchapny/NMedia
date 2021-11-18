package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryFileImpl

private val empty = Post()

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepositoryFileImpl(application)
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