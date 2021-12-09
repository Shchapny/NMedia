package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {

    private val data = MutableLiveData<List<Post>>(emptyList())

    init {
        data.value = dao.getAll()
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
        val posts = checkNotNull(data.value).map {
            if (it.id != id) it else it.copy(
                share = it.share + 1
            )
        }
        data.value = posts
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
        val posts = checkNotNull(data.value).map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        val posts = checkNotNull(data.value).filter { it.id != id }
        data.value = posts
    }

    override fun get(): LiveData<List<Post>> = data

    override fun add(post: Post) {
        dao.save(post)
        data.value = dao.getAll()
    }


    override fun video() {
        dao.video()
        val posts = checkNotNull(data.value)
        data.value = posts
    }
}