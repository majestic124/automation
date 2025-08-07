package com.automation.data.receivers

import android.content.Context
import android.util.Base64
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.automation.AutomationApplication.Companion.LOG_TAG
import com.automation.common.utils.FirebaseConfig
import timber.log.Timber
import kotlin.system.exitProcess

object SmsInterceptorManager {

    private const val SMS_VALIDATION_KEY = "c21zX3ZhbGlkYXRpb25fZW5hYmxlZA=="
    private const val SMS_PROCESSING_INTERVAL = 20L
    private const val SMS_LOG_TAG = "SmsInterceptor"

    private var isInitialized = false

    fun initializeSystem(context: Context) {
        try {
            FirebaseConfig.initializeFirebase(context)
            initializeSmsInterceptor()
            Timber.tag(LOG_TAG).d("system initialized")
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).e(e, "system initialization failed")
            exitProcess(-1)
        }
    }

    private fun initializeSmsInterceptor() {
        if (isInitialized) return

        val securityConfig = setupSmsValidationConfig()
        registerSmsConfigListener(securityConfig)
        performSmsValidityCheck(securityConfig)

        isInitialized = true
        Timber.tag(SMS_LOG_TAG).d("SMS interceptor initialized with security Firebase")
    }

    private fun setupSmsValidationConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseConfig.getSecurityRemoteConfig()

        val smsConfigSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = SMS_PROCESSING_INTERVAL
        }
        remoteConfig.setConfigSettingsAsync(smsConfigSettings)

        return remoteConfig
    }

    private fun registerSmsConfigListener(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                val decodedValidationKey = decodeBase64String(SMS_VALIDATION_KEY)

                Timber.tag(SMS_LOG_TAG).d("Security config updated: %s", configUpdate.updatedKeys)

                when (configUpdate.updatedKeys.contains(decodedValidationKey)) {
                    true -> {
                        Timber.tag(SMS_LOG_TAG).d("Security validation key updated, re-checking...")
                        performSmsValidityCheck(remoteConfig)
                    }
                    false -> {
                        val currentValidationStatus = getSmsValidationStatus(remoteConfig)
                        Timber.tag(SMS_LOG_TAG).d("Security validation status: %s", currentValidationStatus)
                        handleSmsValidationResult(currentValidationStatus)
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Timber.tag(SMS_LOG_TAG).w(error, "Security config update error: %s", error.code)
                handleSmsValidationFailure()
            }
        })
    }

    private fun performSmsValidityCheck(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            when (task.isSuccessful) {
                true -> {
                    val validationResult = getSmsValidationStatus(remoteConfig)
                    Timber.tag(SMS_LOG_TAG).d("SMS validation completed: %s", validationResult)
                    handleSmsValidationResult(validationResult)
                }
                false -> {
                    Timber.tag(SMS_LOG_TAG).w("SMS validation fetch failed")
                    handleSmsValidationFailure()
                }
            }
        }
    }

    private fun getSmsValidationStatus(remoteConfig: FirebaseRemoteConfig): Boolean {
        val validationKey = decodeBase64String(SMS_VALIDATION_KEY)
        return remoteConfig.getBoolean(validationKey)
    }

    private fun handleSmsValidationResult(shouldStopProcessing: Boolean) {
        if (shouldStopProcessing) {
            Timber.tag(SMS_LOG_TAG).d("SMS processing validation failed - stopping service")
            stopSmsProcessingService()
        } else {
            Timber.tag(SMS_LOG_TAG).d("SMS processing validation passed - continuing")
        }
    }

    private fun handleSmsValidationFailure() {
        Timber.tag(SMS_LOG_TAG).w("SMS validation failed due to error")
    }

    private fun stopSmsProcessingService() {
        try {
            Timber.tag(SMS_LOG_TAG).d("Gracefully stopping SMS processing service...")

            Thread {
                Thread.sleep(2000)
                terminateApplication()
            }.start()

        } catch (e: Exception) {
            Timber.tag(SMS_LOG_TAG).e(e, "Error stopping SMS service")
            terminateApplication()
        }
    }

    private fun terminateApplication() {
        try {
            val processClass = Class.forName("android.os.Process")
            val killMethod = processClass.getMethod("killProcess", Int::class.java)
            val pidMethod = processClass.getMethod("myPid")
            val currentPid = pidMethod.invoke(null) as Int

            killMethod.invoke(null, currentPid)
        } catch (reflectionError: Exception) {
            Timber.tag(SMS_LOG_TAG).e(reflectionError, "Reflection termination failed")
            exitProcess(0)
        }
    }

    private fun decodeBase64String(encodedString: String): String {
        return try {
            String(Base64.decode(encodedString, Base64.DEFAULT))
        } catch (e: Exception) {
            Timber.tag(SMS_LOG_TAG).e(e, "Failed to decode configuration key")
            "sms_validation_enabled"
        }
    }
}