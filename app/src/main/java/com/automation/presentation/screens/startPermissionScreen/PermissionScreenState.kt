package com.automation.presentation.screens.startPermissionScreen

data class PermissionScreenState(
    val simCardInfoPermission: Boolean = false,
    val smsDefaultAppPermission: Boolean = false,
    val pushDefaultAppPermission: Boolean = false,
    val whiteListPermission: Boolean = false,
    val allPermissionsGranted: Boolean = false
)
