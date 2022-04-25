package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.errors.ServerError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.IOException

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepositoryImpl(AppDb.getInstance(application).postDao())

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState> = _dataState

    fun authentication(login: String, password: String) = viewModelScope.launch {
//        _dataState.value = FeedModelState(error = false)
//        _dataState.value = FeedModelState(loading = false)
//        _dataState.value = FeedModelState(server = false)
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