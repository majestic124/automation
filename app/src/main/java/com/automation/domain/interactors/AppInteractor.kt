package com.automation.domain.interactors

import com.automation.data.database.entity.MessageEntity
import com.automation.data.models.Message
import com.automation.data.models.Ping
import com.automation.domain.datasource.AllApplicationDataSource
import com.automation.domain.datasource.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

class AppInteractor(
    private val appRepository: AllApplicationDataSource,
    private val localRepository: LocalDataSource,
    private val json: Json
) {
    var isLoggedIn: Boolean by appRepository::isLoggedIn
    var hasPerms: Boolean by appRepository::hasPerms
    var token: String by appRepository::token
    var isBroadcastSmsGranted: Boolean by appRepository::isBroadcastSmsGranted
    var isExactAlarmsGranted: Boolean by appRepository::isExactAlarmsGranted
    var isWriteStorage: Boolean by appRepository::isWriteStorage
    var isReadPhoneState: Boolean by appRepository::isReadPhoneState
    var isReadPhone: Boolean by appRepository::isReadPhone
    var isDefaultSmsApp: Boolean by appRepository::isDefaultSmsApp
    var userPhoneNumber: String? by appRepository::userPhoneNumber
    var isNeedStartNotifService: Boolean by appRepository::isNeedStartNotifService
    var notificationListenerEnable: Boolean by appRepository::notificationListenerEnabled

    // Ping check point
    fun getPingStatusFlow(): Flow<Boolean> {
        return appRepository.isPingStatusProperty.asFlow()
    }

    suspend fun sendMessages(
        message: Message,
    ): Boolean {
        val request = appRepository.sendMessage(message)
        if (!request.isSuccessful) {
            throw Exception("Network call failed with error code ${request.code()}")
        }
        return request.isSuccessful
    }

    suspend fun ping(phone: Ping): Boolean {
        val request = appRepository.ping(phone)
        appRepository.isPingStatusProperty.updateValue(request.isSuccessful)
        if (!request.isSuccessful) {
            throw Exception("Network call failed with error code ${request.code()}")
        }
        return request.isSuccessful
    }

    suspend fun updatePingStatusManually(status: Boolean) {
        appRepository.isPingStatusProperty.updateValue(status)
    }

    suspend fun saveMessage(
        msg: String,
        msgType: String,
        phoneNumber: String,
        receivedAt: String,
        isFailed: Boolean,
        receivedDate: String,
        dispatchDate: String? = null,
        receipePhoneNumber: String?,
        simSlotIndex: Int?,
        senderTitle: String? = null
    ) {
        localRepository.saveMessage(
            msg,
            msgType,
            phoneNumber,
            receivedAt,
            isFailed,
            receivedDate,
            dispatchDate,
            receipePhoneNumber,
            simSlotIndex,
            senderTitle
        )
    }

    suspend fun getMessages(): List<MessageEntity> {
        return localRepository.getMessages()
    }

    suspend fun getMessagesFlow(): Flow<List<MessageEntity>> {
        return localRepository.getMessagesFlow()
    }

    suspend fun getFailedMessage(): List<MessageEntity> {
        return localRepository.getFailedMessages()
    }

    suspend fun updateMessageDispatchDate(messageId: Long, dispatchDate: String) {
        localRepository.updateMessageSendTime(messageId, dispatchDate)
    }

    suspend fun updateMessages(vararg messages: MessageEntity) {
        localRepository.updateMessages(*messages)
    }
}