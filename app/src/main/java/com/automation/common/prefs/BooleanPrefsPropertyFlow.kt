package com.automation.common.prefs

import android.content.SharedPreferences
import com.automation.common.prefs.BooleanPrefsProperty
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BooleanPrefsPropertyWithFlow(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: Boolean
) : BooleanPrefsProperty(prefs, key, defaultValue) {

    fun asFlow(): Flow<Boolean> = prefs.booleanFlow(key, defaultValue)

    fun updateValue(newValue: Boolean) {
        prefs.edit().putBoolean(key, newValue).apply()
    }
}

fun SharedPreferences.booleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
    callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getBoolean(key, defaultValue))
            }
        }
        registerOnSharedPreferenceChangeListener(listener)
        trySend(getBoolean(key, defaultValue))

        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
