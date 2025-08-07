package com.automation.common.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


//Common
fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun Context.getDefaultSharedPreferences(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob()).launch(context) {
        try {
            block()
        } finally {
            pendingResult?.finish()
        }
    }
}

fun Context.restartApplication() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val componentName = intent?.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun Context.getAppVersionName(): String {
    return try {
        val packageInfo =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}
