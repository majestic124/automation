package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class StringPrefsProperty(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: String
) : PrefsProperty<String>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        prefs.edit { putString(key, value) }
    }
}