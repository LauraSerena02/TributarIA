package com.example.tributaria.features.news.di

import com.example.tributaria.features.news.provider.NewsProvider
import com.example.tributaria.features.news.repository.NewsRepository
import com.example.tributaria.features.news.repository.NewsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providerNewsRepository(provider: NewsProvider): NewsRepository =
        NewsRepositoryImp(provider)
}

