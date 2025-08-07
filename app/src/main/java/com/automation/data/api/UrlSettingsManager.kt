package com.automation.data.api

import android.content.Context
import android.content.SharedPreferences
import com.automation.R

class UrlSettingsManager(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {

    private val defaultBaseUrl: String
        get() = context.getString(R.string.default_base_url)

    fun getBaseUrl(): String {
        val url = sharedPreferences.getString(KEY_BASE_URL, defaultBaseUrl) ?: defaultBaseUrl
        return if (!url.startsWith(PREFIX)) {
            ensureSingleSlashSuffix("$PREFIX$url")
        } else {
            ensureSingleSlashSuffix(url)
        }
    }

    fun setBaseUrl(baseUrl: String) {
        val url = if (!baseUrl.startsWith("https://")) {
            ensureSingleSlashSuffix("$PREFIX$baseUrl")
        } else {
            ensureSingleSlashSuffix(baseUrl)
        }
        sharedPreferences.edit().putString(KEY_BASE_URL, url).apply()
    }

    private fun ensureSingleSlashSuffix(url: String): String {
        return url.trimEnd('/') + "/"
    }

    companion object {
        private const val KEY_BASE_URL = "key_base_url"
        private const val PREFIX = "https://"
    }
}