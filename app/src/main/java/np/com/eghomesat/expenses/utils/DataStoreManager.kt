package np.com.eghomesat.expenses.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val IS_PIN_ENABLED = booleanPreferencesKey("is_pin_enabled")
        val USER_PIN = stringPreferencesKey("user_pin")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val isPinEnabled: Flow<Boolean> = context.dataStore.data.map { it[IS_PIN_ENABLED] ?: false }
    val userPin: Flow<String?> = context.dataStore.data.map { it[USER_PIN] }
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }

    suspend fun setPinEnabled(enabled: Boolean) {
        context.dataStore.edit { it[IS_PIN_ENABLED] = enabled }
    }

    suspend fun setUserPin(pin: String) {
        context.dataStore.edit { it[USER_PIN] = pin }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = enabled }
    }
}
