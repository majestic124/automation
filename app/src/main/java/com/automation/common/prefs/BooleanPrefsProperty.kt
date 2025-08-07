package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

open class BooleanPrefsProperty(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: Boolean
) : PrefsProperty<Boolean>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        return prefs.edit { putBoolean(key, value) }
    }
}