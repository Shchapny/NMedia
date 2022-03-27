package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post()

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data = repository.data
        .map { FeedModel(it, it.isEmpty()) }
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private var nameError: PlaceOfError? = null
    private var postId: Long? = null

    val newerCount = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun like(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

//    fun share(id: Long) = viewModelScope.launch {
//        try {
//            repository.shareById(id)
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }

    fun remove(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
        refresh()
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
            _postCreated.postValue(Unit)
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

//    fun video(post: Post) = repository.video()

//    fun unlikeById(id: Long) = viewModelScope.launch {
//        try {
//            repository.unlikeById(id)
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }

    fun refresh() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun retry() {
        when (nameError) {
            PlaceOfError.LIKE -> postId?.let { like(it) }
            PlaceOfError.REMOVE -> postId?.let { remove(it) }
            PlaceOfError.SAVE -> save()
//            PlaceOfError.DISLIKE -> postId?.let { unlikeById(it) }
            else -> loadPosts()
        }
    }

    fun getNewPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: java.lang.Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    enum class PlaceOfError {
        LIKE,
        REMOVE,
        SAVE,
        DISLIKE
    }
}