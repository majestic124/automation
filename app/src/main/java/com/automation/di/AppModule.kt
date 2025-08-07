package com.automation.di

import androidx.room.Room
import com.automation.common.permission.AllPermissionStateProvider
import com.automation.common.permission.permissions.ExactAlarmsPermission
import com.automation.common.permission.permissions.NotificationListenerPermission
import com.automation.common.permission.permissions.ReadExternalStoragePermission
import com.automation.common.permission.permissions.ReadPhoneNumberPermission
import com.automation.common.permission.permissions.ReadPhoneStatePermission
import com.automation.common.permission.permissions.SmsReceivePermission
import com.automation.common.permission.permissions.WriteExternalStoragePermission
import com.automation.common.utils.getDefaultSharedPreferences
import com.automation.data.database.MigrationsProvider
import com.automation.data.database.KeeperDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single { androidContext().getDefaultSharedPreferences() }
    single { SmsReceivePermission(context = androidContext()) }
    single { WriteExternalStoragePermission(context = androidContext()) }
    single { ReadExternalStoragePermission(context = androidContext()) }
    single { ExactAlarmsPermission(context = androidContext()) }
    single { AllPermissionStateProvider(context = androidContext()) }
    single { NotificationListenerPermission(context = androidContext()) }
    single { ReadPhoneStatePermission(context = androidContext()) }
    single { ReadPhoneNumberPermission(context = androidContext()) }

    single {
        Room.databaseBuilder(
            context = androidContext(),
            KeeperDatabase::class.java,
            KeeperDatabase.SMS_KEEPER_DB
        ).addMigrations(
            *MigrationsProvider().migrations
        ).build()
    }
    single { get<KeeperDatabase>().messagesDao() }
}