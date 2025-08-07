package com.automation.presentation.screens.startPermissionScreen

sealed interface PermissionAction {
    data class SimCardPermissionAction(val isChecked: Boolean) : PermissionAction
    data class SmsDefaultPermissionAction(val isChecked: Boolean) : PermissionAction
    data class PushDefaultPermissionAction(val isChecked: Boolean) : PermissionAction
    data class WhiteListPermissionAction(val isChecked: Boolean) : PermissionAction
}