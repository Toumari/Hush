package com.hush.app.di

import android.content.Context
import com.hush.app.audio.SoundEngine
import com.hush.app.billing.BillingManager
import com.hush.app.data.SoundRepository
import com.hush.app.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSoundRepository(): SoundRepository = SoundRepository()

    @Provides
    @Singleton
    fun provideSoundEngine(): SoundEngine = SoundEngine()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context,
        userPreferences: UserPreferences
    ): BillingManager = BillingManager(context, userPreferences)
}
