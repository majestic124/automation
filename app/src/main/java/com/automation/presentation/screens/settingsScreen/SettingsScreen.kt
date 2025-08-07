package com.automation.presentation.screens.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.automation.BuildConfig
import com.automation.R
import com.automation.common.utils.restartApplication
import com.automation.presentation.MainViewModel
import com.automation.presentation.ui.core.PrimaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = koinViewModel(),
    mainViewModel: MainViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val appVersion = BuildConfig.VERSION_NAME
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(state.toastMessage) {
        if (!state.toastMessage.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(
                message = state.toastMessage ?: "",
                duration = SnackbarDuration.Long
            )
        }
    }

    Column(
        modifier = modifier.statusBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),  // Ensure text is on top if overlapping
                    contentAlignment = Alignment.Center  // Centers content in the Box
                ) {
                    Text(
                        text = "Настройки",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = colorScheme.onSurface
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { mainViewModel.openAllSmsScreen() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.background
            )
        )
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Версия: $appVersion",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onBackground // Используем цвет для текста
                )
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_url_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = state.urlLink ?: "",
                        onValueChange = settingsViewModel::updateLink,
                        label = {
                            Text(
                                text = stringResource(id = R.string.settings_url_title),
                                color = colorScheme.onSurface
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        )
                    )
                }

            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_phone_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = state.userPhoneNumber ?: "",
                        onValueChange = settingsViewModel::updatePhone,
                        label = {
                            Text(
                                text = stringResource(id = R.string.settings_phone_title),
                                color = colorScheme.onSurface
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        )
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.padding(
                        vertical = 12.dp
                    )
                ) {
                    PrimaryButton(
                        text = stringResource(id = R.string.next_button_title),
                        onClick = {
                            scope.launch {
                                if (settingsViewModel.clickSaveButton()) {
                                    delay(1.seconds)
                                    context.applicationContext.restartApplication()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

