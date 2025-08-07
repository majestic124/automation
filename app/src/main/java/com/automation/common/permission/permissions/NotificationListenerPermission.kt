package com.automation.common.permission.permissions

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils

class NotificationListenerPermission(private val context: Context) {

    companion object {
        const val PERMISSION_NAME = "enabled_notification_listeners"
    }

    fun checkPermission(): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            PERMISSION_NAME
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (name in names) {
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}