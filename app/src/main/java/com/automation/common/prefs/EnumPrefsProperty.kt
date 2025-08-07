package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class EnumPrefsProperty<T : Enum<T>>(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: T,
    private val values: Array<T>
) : PrefsProperty<T>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return values[prefs.getInt(key, defaultValue.ordinal)]
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        prefs.edit { putInt(key, value.ordinal) }
    }
}