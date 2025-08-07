package com.automation.common.permission.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.automation.common.permission.PermissionManager

class ReadPhoneNumberPermission(context: Context): PermissionManager(context) {
    override val permission: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Manifest.permission.READ_PHONE_NUMBERS
}