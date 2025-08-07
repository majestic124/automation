package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class IntPrefsProperty(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: Int
) : PrefsProperty<Int>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        prefs.edit { putInt(key, value) }
    }
}