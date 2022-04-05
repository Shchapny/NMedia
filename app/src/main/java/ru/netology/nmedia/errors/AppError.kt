package ru.netology.nmedia.errors

import ru.netology.nmedia.R
import java.io.IOException
import java.sql.SQLException

sealed class AppError(val code: Int, info: String): RuntimeException(info) {
    companion object {
        fun from(e: Throwable): AppError = when (e) {
            is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            else -> UnknownError
        }
    }
}

class ApiError(code: Int, message: String): AppError(code, message)

object NetworkError: AppError(-1, "${R.string.error_network}")

object UnknownError: AppError(-1, "${R.string.error_unknown}")

object DbError: AppError(-1, "${R.string.db_error}")