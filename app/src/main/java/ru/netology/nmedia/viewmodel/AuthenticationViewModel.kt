package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.errors.ServerError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val repository: PostRepository) :
    ViewModel() {

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    fun authentication(login: String, password: String) = viewModelScope.launch {
        try {
            repository.authentication(login, password)
            _dataState.value = FeedModelState(authState = true)
        } catch (e: ServerError) {
            _dataState.value = FeedModelState(server = true)
        } catch (e: NetworkError) {
            _dataState.value = FeedModelState(loading = true)
        } catch (e: ApiError) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun reset() {
        _dataState.value = FeedModelState()
    }
}