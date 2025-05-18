package com.example.tributaria.features.calendar.di

import android.content.Context
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import androidx.room.Room
import androidx.work.WorkManager
import com.example.tributaria.features.calendar.domain.usecase.ScheduleRemindersUseCase
import com.example.tributaria.features.calendar.repository.AppDatabase
import com.example.tributaria.features.calendar.repository.ReminderDao
import com.example.tributaria.features.calendar.repository.ReminderRepository
import com.example.tributaria.features.calendar.repository.ReminderRepositoryImpl
import com.example.tributaria.features.calendar.util.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context).apply {
            createNotificationChannel()
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tributaria-db"
        )
            .fallbackToDestructiveMigration() // Manejo de migraciones
            .build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(db: AppDatabase): ReminderDao {
        return db.reminderDao()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        dao: ReminderDao,
        @ApplicationContext context: Context
    ): ReminderRepository {
        return ReminderRepositoryImpl(dao, context)
    }

    @Provides
    @Singleton
    fun provideScheduleRemindersUseCase(
        workManager: WorkManager,
        reminderRepository: ReminderRepository
    ): ScheduleRemindersUseCase {
        return ScheduleRemindersUseCase(workManager, reminderRepository)
    }
}