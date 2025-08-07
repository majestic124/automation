package com.automation.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.automation.presentation.navigation.Screen
import com.automation.presentation.screens.allMessagesListScreen.AllMessagesScreen
import com.automation.presentation.screens.settingsScreen.SettingsScreen
import com.automation.presentation.screens.startPermissionScreen.PermissionScreen
import com.automation.presentation.ui.theme.AutoKeeperTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel() { parametersOf(this) }
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                AutoKeeperTheme {
                    navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(Unit) {
                        setupObservers()
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.windowInsetsPadding(
                                    WindowInsets.ime
                                )
                            )
                        }
                    ) { innerPadding ->
                        AppNavigation(
                            navController = navController,
                            modifier = Modifier,
                            innerPadding = innerPadding,
                            mainViewModel = mainViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        mainViewModel.mainNavState.observe(this) { navState ->
            when (navState) {
                MainViewModel.MainNavState.Perms -> {
                    navController.navigate(Screen.PermissionScreen.route)
                }

                MainViewModel.MainNavState.Sms -> {
                    mainViewModel.startWorkManager()
                    navController.navigate(Screen.AllSmsScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                MainViewModel.MainNavState.Settings -> {
                    navController.navigate(Screen.SettingsScreen.route)
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mainViewModel.removeCallback()
        mainViewModel.checkAndStartWorkManager()
    }

    override fun onDestroy() {
        mainViewModel.removeCallback()
        super.onDestroy()
    }

    @Composable
    fun AppNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        mainViewModel: MainViewModel,
        innerPadding: PaddingValues,
        snackbarHostState: SnackbarHostState
    ) {
        val colorScheme = MaterialTheme.colorScheme

        NavHost(
            navController = navController,
            startDestination = mainViewModel.getStartDestination(),
            modifier = modifier
        ) {
            composable(Screen.AllSmsScreen.route) {
                AllMessagesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.background),
                    mainViewModel = mainViewModel
                )
            }
            composable(Screen.PermissionScreen.route) {
                PermissionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.background)
                        .padding(innerPadding)
                        .padding(16.dp),
                    mainViewModel = mainViewModel
                )
            }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.background),
                    mainViewModel = mainViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
