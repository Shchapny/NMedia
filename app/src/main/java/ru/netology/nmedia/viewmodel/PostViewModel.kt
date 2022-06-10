package ru.netology.nmedia.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.enumeration.DateType.*
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.DatePublished
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

private val empty = Post()

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PostRepository
) :
    ViewModel() {

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel> = _photo

    private val cached = repository.data
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->

                val beforePublished = DatePublished.getDatePublished(before?.published)
                val afterPublished = DatePublished.getDatePublished(after?.published)

                if (beforePublished == NULL) {
                    when (afterPublished) {
                        TODAY -> dateSeparator(R.string.today)
                        YESTERDAY -> dateSeparator(R.string.yesterday)
                        LONG_AGO -> dateSeparator(R.string.week_ago)
                        NULL -> null
                    }
                } else null
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, _ ->
                if (before?.id?.rem(5) == 0L) {
                    ad("figma.jpg")
                } else null
            }
        }
        .cachedIn(viewModelScope)

    private fun dateSeparator(resource: Int): FeedItem {
        return DateSeparator(
            id = Random.nextLong(),
            published = context.getString(resource)
        )
    }

    private fun ad(text: String): FeedItem {
        return Ad(
            id = Random.nextLong(),
            image = text
        )
    }

    val data = cached

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
            null -> error("Unknown error cause")
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