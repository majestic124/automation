package com.automation.presentation.navigation


sealed class Screen(val route: String) {
    data object AllSmsScreen : Screen("AllSmsScreen")
    data object PermissionScreen : Screen("PermissionScreen")
    data object SettingsScreen : Screen("SettingsScreen")
}