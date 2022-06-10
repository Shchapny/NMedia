package ru.netology.nmedia.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormat {
    fun dateFormat(value: Long): String {
        val formatDate = SimpleDateFormat("dd MMMM yyyy г.", Locale.getDefault())
        return formatDate.format(Date(value))
    }
}