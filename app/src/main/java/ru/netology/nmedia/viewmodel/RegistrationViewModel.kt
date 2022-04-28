package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.errors.ServerError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    private val _photo = MutableLiveData(PhotoModel())
    val photo: LiveData<PhotoModel> = _photo

    fun registrationWithoutAvatars(login: String, password: String, name: String) = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = false)
        _dataState.value = FeedModelState(server = false)
        try {
            repository.registration(login, password, name)
            _dataState.value = FeedModelState(authState = true)
        } catch (e: ServerError) {
            _dataState.value = FeedModelState(server = true)
        } catch (e: NetworkError) {
            _dataState.value = FeedModelState(loading = true)
        }
    }

    fun registrationWithAvatars(login: String, password: String, name: String, avatar: File) =
        viewModelScope.launch {
            _dataState.value = FeedModelState(loading = false)
            _dataState.value = FeedModelState(server = false)
            try {
                repository.registerWithPhoto(login, password, name, MediaUpload(avatar))
                _dataState.value = FeedModelState(authState = true)
            } catch (e: ServerError) {
                _dataState.value = FeedModelState(server = true)
            } catch (e: NetworkError) {
                _dataState.value = FeedModelState(loading = true)
            }
        }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri = uri, file = file)
    }

    fun reset() {
        _dataState.value = FeedModelState()
    }
}