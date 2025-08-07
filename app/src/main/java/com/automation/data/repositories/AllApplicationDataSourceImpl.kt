package com.automation.data.repositories

import android.content.SharedPreferences
import com.automation.common.prefs.BooleanPrefsProperty
import com.automation.common.prefs.BooleanPrefsPropertyWithFlow
import com.automation.common.prefs.StringPrefsProperty
import com.automation.common.prefs.StringPrefsPropertyNullable
import com.automation.data.api.KeeperService
import com.automation.data.models.Message
import com.automation.data.models.Ping
import com.automation.domain.datasource.AllApplicationDataSource
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_HAS_PERMS
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_BROADCAST_SMS_GRANTED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_EXACT_ALARMS_GRANTED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_LOGGED_IN
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_TOKEN
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_WRITE_STORAGE_GRANTED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_READ_PHONE_STATE_GRANTED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_READ_PHONE_GRANTED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_DEFAULT_SMS_APP
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_USER_PHONE_NUMBER
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_NEED_START_NOTIF_SERVICE
import com.automation.domain.datasource.AllApplicationDataSource.Companion.NOTIFICATION_LISTENER_ENABLED
import com.automation.domain.datasource.AllApplicationDataSource.Companion.PREF_IS_PING_STATUS
import retrofit2.Response

class AllApplicationDataSourceImpl(
    private val preferences: SharedPreferences,
    private val service: KeeperService
) : AllApplicationDataSource {
    override var isLoggedIn: Boolean by BooleanPrefsProperty(preferences, PREF_IS_LOGGED_IN, false)
    override var hasPerms: Boolean by BooleanPrefsProperty(preferences, PREF_HAS_PERMS, false)
    override var token: String by StringPrefsProperty(preferences, PREF_TOKEN, "")
    override var isBroadcastSmsGranted: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_BROADCAST_SMS_GRANTED,
        false
    )
    override var isExactAlarmsGranted: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_EXACT_ALARMS_GRANTED,
        false
    )
    override var isWriteStorage: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_WRITE_STORAGE_GRANTED,
        false
    )
    override var isReadPhoneState: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_READ_PHONE_STATE_GRANTED,
        false
    )
    override var isReadPhone: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_READ_PHONE_GRANTED,
        false
    )
    override var isDefaultSmsApp: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_DEFAULT_SMS_APP,
        false
    )
    override var userPhoneNumber: String? by StringPrefsPropertyNullable(
        preferences,
        PREF_USER_PHONE_NUMBER,
        null
    )

    override var isNeedStartNotifService: Boolean by BooleanPrefsProperty(
        preferences,
        PREF_IS_NEED_START_NOTIF_SERVICE,
        true
    )

    override var notificationListenerEnabled: Boolean by BooleanPrefsProperty(
        preferences,
        NOTIFICATION_LISTENER_ENABLED,
        false
    )

    override suspend fun sendMessage(msg: Message): Response<Unit> {
        return service.sendMessage(
            data = msg
        )
    }

    override suspend fun ping(ping: Ping): Response<Unit> {
        return service.ping(
            data = ping
        )
    }

    override var isPingStatusProperty = BooleanPrefsPropertyWithFlow(
        preferences,
        PREF_IS_PING_STATUS,
        false
    )

}