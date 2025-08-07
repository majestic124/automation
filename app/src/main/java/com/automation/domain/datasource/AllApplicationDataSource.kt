package com.automation.domain.datasource

import com.automation.common.prefs.BooleanPrefsPropertyWithFlow
import com.automation.data.models.Message
import com.automation.data.models.Ping
import retrofit2.Response

interface AllApplicationDataSource {

    companion object {
         const val PREF_IS_LOGGED_IN = "AutomationRepository.is_logged_in"
         const val PREF_HAS_PERMS = "AutomationRepository.has_perms"
         const val PREF_TOKEN = "AutomationRepository.token"
         const val PREF_IS_BROADCAST_SMS_GRANTED = "AutomationRepository.is_broadcast_sms_granted"
         const val PREF_IS_EXACT_ALARMS_GRANTED = "AutomationRepository.is_exact_alarms_granted"
         const val PREF_IS_WRITE_STORAGE_GRANTED = "AutomationRepository.is_write_storage_granted"
         const val PREF_IS_READ_PHONE_STATE_GRANTED = "AutomationRepository.is_read_phone_state_granted"
         const val PREF_IS_READ_PHONE_GRANTED = "AutomationRepository.is_read_phone_granted"
         const val PREF_IS_DEFAULT_SMS_APP = "AutomationRepository.is_default_sms_app"
         const val PREF_USER_PHONE_NUMBER = "AutomationRepository.user_phone_number"
         const val PREF_IS_NEED_START_NOTIF_SERVICE = "AutomationRepository.isNeedStartNotifService"
         const val NOTIFICATION_LISTENER_ENABLED = "AutomationRepository.notification_listener_enabled"
         const val PREF_IS_PING_STATUS = "AutomationRepository.PREF_IS_PING_STATUS"
    }

    var isLoggedIn: Boolean
    var hasPerms: Boolean
    var token: String
    var isBroadcastSmsGranted: Boolean
    var isExactAlarmsGranted: Boolean
    var isWriteStorage: Boolean
    var isReadPhoneState: Boolean
    var isReadPhone: Boolean
    var isDefaultSmsApp: Boolean
    var userPhoneNumber: String?
    var isNeedStartNotifService: Boolean
    var notificationListenerEnabled: Boolean
    var isPingStatusProperty: BooleanPrefsPropertyWithFlow

    suspend fun sendMessage(msg: Message): Response<Unit>
    suspend fun ping(ping: Ping): Response<Unit>
}