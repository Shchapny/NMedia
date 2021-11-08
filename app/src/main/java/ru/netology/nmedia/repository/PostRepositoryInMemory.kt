package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Click
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemory : PostRepository {

    private var posts = List(10) {
        Post(
            id = it.toLong(),
            author = "Нетология. Университет интернет-профессий будущего",
            content = "$it Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb\"\n",
            published = "21 мая в 18:36",
            likes = 999,
            share = 1241
        )
    }

    private val clicking = Click()
    private val data = MutableLiveData(posts)

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(share = clicking.addByClicking(it.share))
        }
        data.value = posts
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
    }

    override fun get(): LiveData<List<Post>> = data
}