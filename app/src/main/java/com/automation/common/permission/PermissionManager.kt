package com.automation.common.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class PermissionManager(private val context: Context) {

    companion object {
        private const val RESULT_GRANTED = PackageManager.PERMISSION_GRANTED
    }

    protected abstract val permission: String

    fun requestPermissionOrDoIfGranted(
        requestPermissionLauncher: ActivityResultLauncher<String>,
        onGranted: () -> Unit
    ) {
        if (isPermissionGranted()) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    fun createRequestPermissionLauncher(
        fragment: Fragment,
        onDenied: (() -> Unit)? = null,
        onDeniedForever: (() -> Unit)? = null,
        onGranted: () -> Unit
    ): ActivityResultLauncher<String> = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onGranted()
            isPermissionDeniedForever(fragment) -> onDeniedForever?.invoke()
            else -> onDenied?.invoke()
        }
    }

    fun createRequestPermissionLauncher(
        activity: AppCompatActivity,
        onDenied: (() -> Unit)? = null,
        onDeniedForever: (() -> Unit)? = null,
        onGranted: () -> Unit
    ): ActivityResultLauncher<String> = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onGranted()
            isPermissionDeniedForever(activity) -> onDeniedForever?.invoke()
            else -> onDenied?.invoke()
        }
    }

    fun getPermissionName(): String {
        return permission
    }

    fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == RESULT_GRANTED
    }

    private fun isPermissionDeniedForever(fragment: Fragment): Boolean {
        return !fragment.shouldShowRequestPermissionRationale(permission)
    }

    private fun isPermissionDeniedForever(activity: AppCompatActivity): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}