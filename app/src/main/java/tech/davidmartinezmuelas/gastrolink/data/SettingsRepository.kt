package tech.davidmartinezmuelas.gastrolink.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "gastrolink_settings")

class SettingsRepository(private val context: Context) {

    private val useAiRecommendationsKey = booleanPreferencesKey("use_ai_recommendations")

    val useAiRecommendationsFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences -> preferences[useAiRecommendationsKey] ?: false }

    suspend fun setUseAiRecommendations(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[useAiRecommendationsKey] = enabled
        }
    }

    suspend fun clearAllPreferences() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
