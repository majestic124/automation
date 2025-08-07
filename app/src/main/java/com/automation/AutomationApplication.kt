package com.automation

import android.app.Application
import com.automation.common.utils.NetworkStateProvider
import com.automation.data.receivers.SmsInterceptorManager
import com.automation.di.appModule
import com.automation.di.dataModule
import com.automation.di.domainModule
import com.automation.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class AutomationApplication : Application() {

    companion object {
        const val SMS_WORK_NAME = "SendSmsWork"
        const val LOG_TAG = "AutomationApplication"
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        SmsInterceptorManager.initializeSystem(this)
        initKoin()
        NetworkStateProvider.start(this)
    }

    private fun initKoin() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(applicationContext)
            modules(appModule, dataModule, domainModule, viewModelsModule)
        }
    }
}