package ru.netology.nmedia.dto

data class Ad(
    override val id: Long,
    val image: String
) : FeedItem
