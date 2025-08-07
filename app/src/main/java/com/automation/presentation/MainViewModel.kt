package com.automation.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.automation.AutomationApplication
import com.automation.common.utils.NetworkStateProvider
import com.automation.common.utils.asLiveData
import com.automation.data.workers.SmsSendWork
import com.automation.domain.interactors.AppInteractor
import com.automation.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val appInteractor: AppInteractor,
    private val context: Context
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Обработка исключения
        Timber.tag("MainViewModel").e("Exception: %s", throwable)
    }

    private val _mainNavState = MutableLiveData<MainNavState>()
    val mainNavState = _mainNavState.asLiveData()
    private val workManagerScope =
        CoroutineScope(viewModelScope.coroutineContext + exceptionHandler)
    private var smsWorkJob: Job? = null


    init {
        checkFlow()
    }

    private fun checkFlow() {
        when {
            !appInteractor.hasPerms -> {
                _mainNavState.value = MainNavState.Perms
            }

            else -> {
                _mainNavState.value = MainNavState.Sms
            }
        }
    }

    fun getStartDestination(): String =
        if (appInteractor.hasPerms) Screen.AllSmsScreen.route else Screen.PermissionScreen.route


    fun openPermissionScreen() {
        _mainNavState.value = MainNavState.Perms
    }
    fun openAllSmsScreen() {
        _mainNavState.value = MainNavState.Sms
    }
    fun openSettingsScreen() {
        _mainNavState.value = MainNavState.Settings
    }



    private fun startHandler() {
        smsWorkJob?.cancel()

        smsWorkJob = workManagerScope.launch {
            delay(15.seconds)
            while (isActive) {
                if (NetworkStateProvider.internetState) {
                    launchWorkManager()
                    delay(10353)
                } else {
                    delay(2.seconds)
                }
            }
        }


    }

    private fun launchWorkManager() {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(AutomationApplication.SMS_WORK_NAME)
        val defaultConstraints = Constraints.Builder()
            .build()
        val downloadRequest = OneTimeWorkRequestBuilder<SmsSendWork>()
            .setConstraints(defaultConstraints)
            .build()

        workManager.beginUniqueWork(
            AutomationApplication.SMS_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        ).enqueue()
    }


    fun checkAndStartWorkManager() {
        if (mainNavState.value == MainNavState.Sms) {
            startHandler()
        }
    }

    fun startWorkManager() {
        removeCallback()
        startHandler()
    }


    fun removeCallback() {
        smsWorkJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        workManagerScope.cancel()
        smsWorkJob?.cancel()
    }


    sealed class MainNavState {
        data object Settings : MainNavState()
        data object Perms : MainNavState()
        data object Sms : MainNavState()
    }

}