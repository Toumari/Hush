package com.hush.app.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hush_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val KEY_LAST_MIX = stringPreferencesKey("last_mix")
        private val KEY_LAST_TIMER_MINUTES = stringPreferencesKey("last_timer_minutes")
    }

    val isPremium: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_PREMIUM] ?: false
    }

    suspend fun setPremium(premium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_PREMIUM] = premium
        }
    }

    val lastMix: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_MIX] ?: ""
    }

    suspend fun saveLastMix(mixJson: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_MIX] = mixJson
        }
    }

    val lastTimerMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        (preferences[KEY_LAST_TIMER_MINUTES] ?: "30").toIntOrNull() ?: 30
    }

    suspend fun saveLastTimerMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_TIMER_MINUTES] = minutes.toString()
        }
    }
}
