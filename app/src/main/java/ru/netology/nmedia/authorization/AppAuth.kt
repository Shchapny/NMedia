package ru.netology.nmedia.authorization

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.model.AuthState

private const val AUTH = "auth"
private const val ID_KEY = "id"
private const val TOKEN_KEY = "token"

class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE)
    private val idKey = ID_KEY
    private val tokenKey = TOKEN_KEY

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id = id, token = token))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id = id, token = token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    companion object {
        @Volatile
        private var instance: AppAuth? = null

        private fun buildAuth(context: Context) = AppAuth(context)

        fun getInstance() = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context) = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }
        }
    }
}

