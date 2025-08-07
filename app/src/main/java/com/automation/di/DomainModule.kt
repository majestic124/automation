package com.automation.di

import com.automation.domain.interactors.AppInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
    factory { AppInteractor(appRepository = get(), localRepository = get(), json = get()) }
}