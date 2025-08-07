package com.automation.common.permission.permissions

import android.Manifest
import android.content.Context
import com.automation.common.permission.PermissionManager

class ExactAlarmsPermission(context: Context) : PermissionManager(context) {
    override val permission: String
        get() = Manifest.permission.ACCESS_NOTIFICATION_POLICY
}