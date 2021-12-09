package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun get(): LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override fun add(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }

    override fun video() {
        dao.video()
    }
}