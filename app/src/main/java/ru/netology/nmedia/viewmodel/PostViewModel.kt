package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post()

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val edited = MutableLiveData(empty)
    val data: LiveData<FeedModel>
        get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.get()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun like(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        it.copy(
                            likes = it.likes + 1,
                            likedByMe = true
                        )
                    })
            )
            try {
                repository.likeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun share(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        it.copy(
                            share = it.share + 1
                        )
                    })
            )
            try {
                repository.shareById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun remove(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id })
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }

        }
    }

    fun editContent(text: String) {
        val formatted = text.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = formatted)
    }

    fun add() {
        edited.value?.let {
            thread {
                repository.add(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun video(post: Post) = repository.video()

    fun unlikeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .map {
                        it.copy(
                            likes = it.likes - 1,
                            likedByMe = false
                        )
                    })
            )
            try {
                repository.unlikeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}