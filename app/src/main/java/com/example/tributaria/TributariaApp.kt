package com.example.tributaria

import android.app.Application
import com.example.tributaria.di.successModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TributariaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TributariaApp)
            modules(successModule)
        }
    }
}