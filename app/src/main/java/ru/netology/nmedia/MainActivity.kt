package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        val displayCount = DisplayCount()

        with(binding) {
            viewModel.get().observe(this@MainActivity) { post ->
                author.text = post.author
                content.text = post.content
                published.text = post.published
                textLikes.text = displayCount.display(post.likes)
                textShare.text = displayCount.display(post.share)

                likes.setImageResource(
                    if (post.likedByMe) {
                        R.drawable.ic_baseline_favorite_24
                    } else {
                        R.drawable.ic_baseline_favorite_border_24
                    }
                )
            }

            likes.setOnClickListener {
                viewModel.like()
            }

            share.setOnClickListener {
                viewModel.share()
            }
        }

    }
}