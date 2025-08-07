package com.automation.presentation.screens.startPermissionScreen

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.provider.Telephony
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automation.R
import com.automation.common.permission.permissions.NotificationListenerPermission
import com.automation.common.permission.permissions.ReadPhoneNumberPermission
import com.automation.presentation.MainViewModel
import com.automation.presentation.screens.startPermissionScreen.PermissionAction.PushDefaultPermissionAction
import com.automation.presentation.screens.startPermissionScreen.PermissionAction.SimCardPermissionAction
import com.automation.presentation.screens.startPermissionScreen.PermissionAction.SmsDefaultPermissionAction
import com.automation.presentation.screens.startPermissionScreen.uiItems.PermissionItem
import com.automation.presentation.ui.core.PrimaryButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    viewModel: PermissionViewModel = koinViewModel(),
    mainViewModel: MainViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    val permissionReadPhoneNumbers: ReadPhoneNumberPermission = koinInject()
    val notifServicePerm: NotificationListenerPermission = koinInject()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(state.allPermissionsGranted) {
        if (state.allPermissionsGranted) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    val simCardPermissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onCheckedChangeAction(SimCardPermissionAction(isGranted))
    }

    val smsDefaultAppPermissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> viewModel.onCheckedChangeAction(SmsDefaultPermissionAction(true))
            Activity.RESULT_CANCELED -> viewModel.onCheckedChangeAction(
                SmsDefaultPermissionAction(
                    false
                )
            )
        }
    }

    val notificationPermissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onCheckedChangeAction(PushDefaultPermissionAction(notifServicePerm.checkPermission()))
    }

    val whiteListPermissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val isWhitelisted = isOptimizationSupported(context)
            viewModel.onCheckedChangeAction(PermissionAction.WhiteListPermissionAction(isWhitelisted))
        }
    }


    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Inside
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text(
                text = stringResource(id = R.string.permission_label),
                modifier = Modifier,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onBackground
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Text(
                text = stringResource(id = R.string.permission_sublabel),
                modifier = Modifier,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onBackground
            )
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            Column(
                modifier = Modifier.padding(vertical = 14.dp)
            ) {
                PermissionItem(
                    titleText = stringResource(id = R.string.permission_white_list),
                    subtitleText = stringResource(id = R.string.permission_subtitle_white_list),
                    isChecked = state.whiteListPermission,
                    modifier = Modifier.padding(vertical = 12.dp),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            whiteListPermissionRequester.launch(intent)
                        }
                    }
                )
                PermissionItem(
                    titleText = stringResource(id = R.string.permission_sim_card),
                    subtitleText = stringResource(id = R.string.permission_subtitle_sim_card),
                    isChecked = state.simCardInfoPermission,
                    modifier = Modifier.padding(vertical = 12.dp),
                    onCheckedChange = { isChecked ->
                        if (isChecked) simCardPermissionRequester.launch(permissionReadPhoneNumbers.getPermissionName())
                    }
                )
                PermissionItem(
                    titleText = stringResource(id = R.string.permission_sms_default),
                    subtitleText = stringResource(id = R.string.permission_subtitle_sms_default),
                    isChecked = state.smsDefaultAppPermission,
                    modifier = Modifier.padding(vertical = 12.dp),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            launchSmsDefaultRequest(
                                context,
                                smsDefaultAppPermissionRequester
                            )
                        }
                    }
                )
                PermissionItem(
                    titleText = stringResource(id = R.string.permission_push),
                    subtitleText = stringResource(id = R.string.permission_subtitle_push),
                    isChecked = state.pushDefaultAppPermission,
                    modifier = Modifier.padding(vertical = 12.dp),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            notificationPermissionRequester.launch(intent)
                        } else {
                            viewModel.onCheckedChangeAction(PushDefaultPermissionAction(false))
                        }
                    }
                )
            }
        }
        item {
            Box(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                PrimaryButton(
                    text = stringResource(id = R.string.next_button_title),
                    enabled = state.allPermissionsGranted,
                    onClick = {
                        mainViewModel.openAllSmsScreen()
                    }
                )
            }
        }
    }
}

private fun launchSmsDefaultRequest(
    context: Context,
    permissionRequester: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        if (roleManager?.isRoleAvailable(RoleManager.ROLE_SMS) == true) {
            permissionRequester.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS))
        }
    } else {
        permissionRequester.launch(
            Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).apply {
                putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
            }
        )
    }
}

private fun isOptimizationSupported(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

//if (showSettingsDialog) {
//    AlertDialog(
//        onDismissRequest = { showSettingsDialog = false },
//        title = { Text("Требуется разрешение") },
//        text = { Text("Пожалуйста, предоставьте разрешение для работы с SIM-картой в настройках") },
//        confirmButton = {
//            TextButton(onClick = {
//                showSettingsDialog = false
//                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                    data = Uri.fromParts("package", context.packageName, null)
//                })
//            }) {
//                Text("Настройки")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = { showSettingsDialog = false }) {
//                Text("Отмена")
//            }
//        }
//    )
//}