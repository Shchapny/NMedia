package ru.netology.nmedia.util

import ru.netology.nmedia.enumeration.DateType
import java.util.*

object DatePublished {

    private const val TODAY = 86400
    private const val M_SEC = 1000

    private val dateCurrent = Date().time

    private fun dateDifferentFromCurrent(date: Long): Long {
        return (dateCurrent - date * M_SEC) / TODAY
    }

    fun getDatePublished(date: Long?): DateType {
        if (date == null) return DateType.NULL

        val timePublished = dateDifferentFromCurrent(date)
        return when {
            timePublished in 0..23L -> DateType.TODAY
            timePublished in 24L..47L -> DateType.YESTERDAY
            timePublished >= 48 -> DateType.LONG_AGO
            else -> DateType.NULL
        }
    }
}