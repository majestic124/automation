package com.automation.presentation.screens.startPermissionScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automation.domain.interactors.AppInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val appInteractor: AppInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionScreenState())
    val state = _state.asStateFlow()

    fun onCheckedChangeAction(action: PermissionAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is PermissionAction.SimCardPermissionAction -> {
                    appInteractor.isReadPhoneState = action.isChecked
                    _state.update { it.copy(simCardInfoPermission = action.isChecked) }
                }

                is PermissionAction.SmsDefaultPermissionAction -> {
                    appInteractor.isDefaultSmsApp = action.isChecked
                    _state.update { it.copy(smsDefaultAppPermission = action.isChecked) }
                }

                is PermissionAction.PushDefaultPermissionAction -> {
                    appInteractor.notificationListenerEnable = action.isChecked
                    _state.update { it.copy(pushDefaultAppPermission = action.isChecked) }
                }

                is PermissionAction.WhiteListPermissionAction -> {
                    _state.update { it.copy(whiteListPermission = action.isChecked) }
                }
            }
            checkAllPermissions()
        }
    }

    private fun checkAllPermissions() {
        val allGranted = with(_state.value) {
            simCardInfoPermission && smsDefaultAppPermission && pushDefaultAppPermission && whiteListPermission
        }
        if (allGranted) {
            viewModelScope.launch(Dispatchers.IO) {
                appInteractor.hasPerms = true
            }
        }
        _state.update { it.copy(allPermissionsGranted = allGranted) }
    }
}