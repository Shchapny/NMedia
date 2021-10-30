package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clicking = Click()
        val displayCount = DisplayCount()

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb\"\n",
            published = "21 мая в 18:36",
            likes = 999,
            share = 1241
        )

        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            textLikes.text = displayCount.display(post.likes)
            textShare.text = displayCount.display(post.share)

            likes.setOnClickListener {
                post.likedByMe = !post.likedByMe

                likes.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.ic_baseline_favorite_24
                    } else {
                        R.drawable.ic_baseline_favorite_border_24
                    }
                )
                if (post.likedByMe) {
                    post.likes = clicking.addByClicking(post.likes)
                    textLikes.text = displayCount.display(post.likes)
                } else {
                    post.likes = clicking.deleteByClicking(post.likes)
                    textLikes.text = displayCount.display(post.likes)
                }

            }

            share.setOnClickListener {
                post.share = clicking.addByClicking(post.share)
                textShare.text = displayCount.display(post.share)
            }
        }

    }
}