package ru.netology.nmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PostRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}