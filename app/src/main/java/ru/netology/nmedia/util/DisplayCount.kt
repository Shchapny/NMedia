package ru.netology.nmedia.util

import kotlin.math.ln
import kotlin.math.pow

class DisplayCount {

    fun display(value: Long): String {
        if (value < 1000) return "" + value
        val exp = (ln(value.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", value / 1000.0.pow(exp.toDouble()), "KM"[exp - 1])
    }
}