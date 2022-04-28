package ru.netology.nmedia.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.authorization.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Provides
    @Singleton
    fun provideApiService(appAuth: AppAuth): ApiService {
        return retrofit(okhttp(loggingInterceptor(), authInterceptor(appAuth)))
            .create(ApiService::class.java)
    }
}