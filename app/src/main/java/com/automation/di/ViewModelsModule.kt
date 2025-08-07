package com.automation.di

import com.automation.presentation.MainViewModel
import com.automation.presentation.screens.allMessagesListScreen.AllMessagesViewModel
import com.automation.presentation.screens.settingsScreen.SettingsViewModel
import com.automation.presentation.screens.startPermissionScreen.PermissionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


val viewModelsModule: Module = module {
    viewModel {
        PermissionViewModel(
            appInteractor = get()
        )
    }

    viewModel {
        AllMessagesViewModel(
            appInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            appInteractor = get(),
            urlSettingsManager = get()
        )
    }

    viewModel {
        MainViewModel(
            appInteractor = get(),
            context = androidContext()
        )
    }
}