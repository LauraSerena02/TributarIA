package com.example.tributaria.features.homepage.di

import com.example.tributaria.features.homepage.repository.ExchangeRateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExchangeRateModule {

    @Provides
    @Singleton
    fun provideExchangeRateRepository(): ExchangeRateRepository {
        return ExchangeRateRepository()
    }
}
