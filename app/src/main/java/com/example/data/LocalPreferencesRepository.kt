package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.predictPlayDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "predictplay_local_prefs",
)

data class LocalUserPrefs(
    val username: String = "PredictorOne",
    val coinBalance: Int = 1000,
    val lastDailyClaimMillis: Long = 0L,
    val lossLimit: Int = 500,
)

class LocalPreferencesRepository(private val context: Context) {

    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val COIN_BALANCE = intPreferencesKey("coin_balance")
        val LAST_DAILY_CLAIM = longPreferencesKey("last_daily_claim_millis")
        val LOSS_LIMIT = intPreferencesKey("loss_limit")
    }

    val prefsFlow: Flow<LocalUserPrefs> = context.predictPlayDataStore.data.map { prefs ->
        LocalUserPrefs(
            username = prefs[Keys.USERNAME] ?: "PredictorOne",
            coinBalance = prefs[Keys.COIN_BALANCE] ?: 1000,
            lastDailyClaimMillis = prefs[Keys.LAST_DAILY_CLAIM] ?: 0L,
            lossLimit = prefs[Keys.LOSS_LIMIT] ?: 500,
        )
    }

    suspend fun saveBalance(balance: Int) {
        context.predictPlayDataStore.edit { prefs ->
            prefs[Keys.COIN_BALANCE] = balance.coerceAtLeast(0)
        }
    }

    suspend fun saveDailyClaim(timestampMillis: Long, newBalance: Int) {
        context.predictPlayDataStore.edit { prefs ->
            prefs[Keys.LAST_DAILY_CLAIM] = timestampMillis
            prefs[Keys.COIN_BALANCE] = newBalance.coerceAtLeast(0)
        }
    }

    suspend fun saveUsername(username: String) {
        val sanitized = username.trim().take(20).ifEmpty { "PredictorOne" }
        context.predictPlayDataStore.edit { prefs ->
            prefs[Keys.USERNAME] = sanitized
        }
    }

    suspend fun saveLossLimit(limit: Int) {
        context.predictPlayDataStore.edit { prefs ->
            prefs[Keys.LOSS_LIMIT] = limit.coerceAtLeast(0)
        }
    }

    suspend fun resetAll() {
        context.predictPlayDataStore.edit { prefs ->
            prefs[Keys.COIN_BALANCE] = 1000
            prefs[Keys.LAST_DAILY_CLAIM] = 0L
            prefs[Keys.LOSS_LIMIT] = 500
        }
    }
}
