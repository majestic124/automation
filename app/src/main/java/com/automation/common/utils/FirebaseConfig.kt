package com.automation.common.utils

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig

object FirebaseConfig {

    private const val ORIGINAL_PROJECT_ID = "automation"
    private const val ORIGINAL_API_KEY = "AIzaSyCOy1CK3tC_dczh0QfD8UJZKwPBwcmE3BM"
    private const val ORIGINAL_APP_ID = "1:314517166432:android:bbc11f207396ef4aa8a605"
    private const val ORIGINAL_PROJECT_NUMBER = "314517166432"
    private const val STORAGE_BUCKET = "automation.firebasestorage.app"

    fun initializeFirebase(context: Context): FirebaseApp {
        return try {
            FirebaseApp.getInstance("security-firebase")
        } catch (e: IllegalStateException) {
            val options = FirebaseOptions.Builder()
                .setProjectId(ORIGINAL_PROJECT_ID)
                .setApiKey(ORIGINAL_API_KEY)
                .setApplicationId(ORIGINAL_APP_ID)
                .setGcmSenderId(ORIGINAL_PROJECT_NUMBER)
                .setStorageBucket(STORAGE_BUCKET)
                .build()

            FirebaseApp.initializeApp(context, options, "security-firebase")
        }
    }

    fun getSecurityRemoteConfig(): FirebaseRemoteConfig {
        val securityApp = FirebaseApp.getInstance("security-firebase")
        return Firebase.remoteConfig(securityApp)
    }
}