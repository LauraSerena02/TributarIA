package com.example.tributaria.di



import com.example.tributaria.features.homepage.repository.ExchangeRateRepository
import com.example.tributaria.features.success.viewmodel.SuccessViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val successModule = module {
    single { ExchangeRateRepository() }
    viewModel { SuccessViewModel(get()) }
}