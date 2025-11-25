/*package com.example.tributaria.utils

import com.example.tributaria.features.news.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.news.di.RepositoryModule

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
class FakeRepositoryModule {

    @Provides
    @Singleton
    fun providerNewsRepository(): NewsRepository =
        object : NewsRepository {
            val news = arrayListOf(
                News("Title1", "Content1", "Author1", "Url1", "urlImage1"),
                News("Title2", "Content2", "Author2", "Url2", "urlImage2")
            )

            override suspend fun getNews(country: String): List<News> = news

            override fun getNew(title: String): News = news[0]
        }
}*/