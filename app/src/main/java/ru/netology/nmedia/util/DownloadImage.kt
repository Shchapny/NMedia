package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.loadImage(url: String, argument: String, post: Any?) {
    when (argument) {
        "avatars" -> Glide.with(this)
            .load("$url/avatars/$post")
            .placeholder(R.drawable.ic_loading_48)
            .error(R.drawable.ic_error_48)
            .circleCrop()
            .timeout(10_000)
            .into(this)
        "media" -> Glide.with(this)
            .load("$url/media/$post")
            .timeout(10_000)
            .into(this)
    }
}