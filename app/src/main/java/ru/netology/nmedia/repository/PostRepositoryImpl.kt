package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.authorization.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.errors.*
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService
) :
    PostRepository {

    @Inject
    lateinit var appAuth: AppAuth

    override suspend fun likeById(id: Long) {
        try {
            val likeById = dao.getById(id).likedByMe
            dao.likeById(id)
            val response =
                if (likeById) apiService.dislikeById(id)
                else apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post): Long {
        try {
            val postId = dao.save(PostEntity.fromDto(post))
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            return postId
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): Post =
        try {
            dao.getById(id).toDto()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(dao.countPosts())
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getNewPosts() = dao.getNewPosts()

    override suspend fun uploadMedia(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = uploadMedia(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun authentication(login: String, password: String) {
        try {
            val response = apiService.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authentication =
                response.body() ?: throw ApiError(response.code(), response.message())
            authentication.token?.let { appAuth.setAuth(authentication.id, it) }
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registration(login: String, password: String, name: String) {
        try {
            val response = apiService.registerUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val registration =
                response.body() ?: throw ApiError(response.code(), response.message())
            registration.token?.let { appAuth.setAuth(registration.id, it) }
        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        mediaUpload: MediaUpload
    ) {
        try {
            val loginRequest = login.toRequestBody("text/plain".toMediaType())
            val passRequest = password.toRequestBody("text/plain".toMediaType())
            val nameRequest = name.toRequestBody("text/plain".toMediaType())
            val media = MultipartBody.Part.createFormData(
                "file", mediaUpload.file.name, mediaUpload.file.asRequestBody()
            )

            val response =
                apiService.registerWithPhoto(loginRequest, passRequest, nameRequest, media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e: ApiError) {
            throw e
        } catch (e: SocketTimeoutException) {
            throw ServerError
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override val data = dao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)
}