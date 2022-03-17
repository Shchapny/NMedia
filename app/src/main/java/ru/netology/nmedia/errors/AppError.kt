package ru.netology.nmedia.errors

import ru.netology.nmedia.R
import java.lang.RuntimeException

sealed class AppError(val code: Int, val info: String): RuntimeException(info)

class ApiError(code: Int, message: String): AppError(code, message)

object NetworkError: AppError(-1, "${R.string.error_network}")

object UnknownError: AppError(-1, "${R.string.error_unknown}")
