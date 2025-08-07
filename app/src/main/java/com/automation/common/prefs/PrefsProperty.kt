package com.automation.common.prefs

import android.content.SharedPreferences
import kotlin.reflect.KProperty

abstract class PrefsProperty<T> {
    protected abstract val prefs: SharedPreferences
    protected abstract val key: String
    protected abstract val defaultValue: T

    abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}
