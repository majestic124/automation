package com.automation.common.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class StringPrefsPropertyNullable(
    override val prefs: SharedPreferences,
    override val key: String,
    override val defaultValue: String?
) : PrefsProperty<String?>() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        val value = prefs.getString(key, defaultValue)
        return if (value.isNullOrEmpty()) null else value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        prefs.edit { putString(key, value ?: "") }
    }
}