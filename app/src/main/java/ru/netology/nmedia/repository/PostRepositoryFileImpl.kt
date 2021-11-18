package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Click
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(private val context: Context) : PostRepository {

    private val clicking = Click()

    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    private val gson = Gson()
    private val filename = "posts.json"
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
            }
        } else {
            sync()
        }
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(share = clicking.addByClicking(it.share))
        }
        data.value = posts

        sync()
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id && it.likedByMe) {
                it.copy(likes = clicking.deleteByClicking(it.likes), likedByMe = !it.likedByMe)
            } else if (it.id != id) {
                it
            } else {
                it.copy(likes = clicking.addByClicking(it.likes), likedByMe = !it.likedByMe)
            }
        }
        data.value = posts

        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts

        sync()
    }

    override fun get(): LiveData<List<Post>> = data

    override fun add(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(id = posts.firstOrNull()?.id?.inc() ?: 0)
            ) + posts
            data.value = posts
            return
        }
        posts = posts.map {
            if (it.id == post.id) it.copy(content = post.content) else it
        }
        data.value = posts

        sync()
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}