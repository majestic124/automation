package com.automation.common.permission

import android.content.Context
import com.automation.common.permission.permissions.ExactAlarmsPermission
import com.automation.common.permission.permissions.ReadExternalStoragePermission
import com.automation.common.permission.permissions.ReadPhoneNumberPermission
import com.automation.common.permission.permissions.ReadPhoneStatePermission
import com.automation.common.permission.permissions.SmsReceivePermission
import com.automation.common.permission.permissions.WriteExternalStoragePermission

class AllPermissionStateProvider(context: Context) {

    private val permissions = listOf(
        ExactAlarmsPermission(context),
        ReadExternalStoragePermission(context),
        SmsReceivePermission(context),
        WriteExternalStoragePermission(context),
        ReadPhoneStatePermission(context),
        ReadPhoneNumberPermission(context)
    )

    fun getPermissionStatusString(): String {
        return permissions.joinToString(separator = ";\n") {
            "${it.getPermissionName()}:${it.isPermissionGranted()}"
        }
    }

    fun getPermissionStatus(): Boolean {
        permissions.forEach { perm ->
            if (!perm.isPermissionGranted()) return false
        }
        return true
    }

    fun getPermissionStatusList(): List<String> {
        return permissions.filter { it.isPermissionGranted() }.map { it.getPermissionName() }
    }

//    fun getAnotherPermState(): String {
//        return ExternalNavigationHelper.getAllPermStateString(context)
//    }
//
//    fun getAnotherPermStateList(): List<String> {
//        return ExternalNavigationHelper.getAllPermStateList(context)
//    }

}