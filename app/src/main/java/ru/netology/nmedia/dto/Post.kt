package ru.netology.nmedia.dto

import java.text.SimpleDateFormat
import java.util.*

data class Post(
    val id: Long = 0,
    val author: String = "Нетология. Университет интернет-профессий будущего",
    val content: String = "",
    val published: String = formattedDate,
    val likedByMe: Boolean = false,
    val views: Long = 0,
    val likes: Long = 0,
    val share: Long = 0,
    val video: String? = null
)

val date = Calendar.getInstance().time
val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
val formattedDate = formatter.format(date)