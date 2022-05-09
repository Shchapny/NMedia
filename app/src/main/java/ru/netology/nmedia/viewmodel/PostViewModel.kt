package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.authorization.AppAuth
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) :
    ViewModel() {

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel> = _photo

    private val cached = repository.data.cachedIn(viewModelScope)

    val data = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { posts ->
                posts.map { it.copy(ownedById = it.authorId == myId) }
            }
        }

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    private var nameError: PlaceOfError? = null
    private var postId: Long? = null

//    val newerCount = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { e -> e.printStackTrace() }
//            .asLiveData(Dispatchers.Default)
//    }

    fun like(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun remove(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun editContent(text: String) {
        val formatted = text.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = formatted)
    }

    fun save() {
        edited.value?.let { post ->
            _postCreated.postValue(Unit)
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(post)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(post, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun retry() {
        when (nameError) {
            PlaceOfError.LIKE -> postId?.let { like(it) }
            PlaceOfError.REMOVE -> postId?.let { remove(it) }
            PlaceOfError.SAVE -> save()
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri = uri, file = file)
    }

    enum class PlaceOfError {
        LIKE,
        REMOVE,
        SAVE
    }
}