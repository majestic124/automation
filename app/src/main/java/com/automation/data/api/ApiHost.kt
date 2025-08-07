package com.automation.data.api

import androidx.annotation.StringRes
import com.automation.BuildConfig
import com.automation.R

enum class ApiHost(@StringRes val urlRes: Int) {
    PROD(R.string.base_url), DEV(R.string.base_url);

    companion object {
        fun getApiHost(): Int {
            return if (BuildConfig.DEBUG) {
                DEV.urlRes
            } else {
                PROD.urlRes
            }
        }
    }
}