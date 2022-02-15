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
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun like(id: Long) {
        repository.likeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(post: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                it.copy(
                                    likes = it.likes + 1,
                                    likedByMe = true
                                )
                            } else {
                                it
                            }
                        })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun share(id: Long) {
        repository.shareById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(post: Post) {
                /*
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) {
                                    it.copy(
                                        share = it.share + 1
                                    )
                                } else {
                                    it
                                }
                            })
                    )
                    */
            }


            override fun onError(e: Exception) {
                /*
                    _data.postValue(FeedModel(error = true))

                 */
            }
        })
    }

    fun remove(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter {
                            it.id != id
                        })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun editContent(text: String) {
        val formatted = text.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = formatted)
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    edited.value = empty
                    _data.postValue(FeedModel(error = true))
                }
            })
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun video(post: Post) = repository.video()

    fun unlikeById(id: Long) {
        repository.unlikeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(post: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) {
                                it.copy(
                                    likes = it.likes - 1,
                                    likedByMe = false
                                )
                            } else {
                                it
                            }
                        })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun refresh() {
        _data.value = FeedModel(refreshing = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
}