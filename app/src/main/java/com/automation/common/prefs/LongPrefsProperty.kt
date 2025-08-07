package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class LongPrefsProperty(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: Long
) : PrefsProperty<Long>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        prefs.edit { putLong(key, value) }
    }
}