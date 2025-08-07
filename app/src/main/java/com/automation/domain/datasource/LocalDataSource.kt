package com.automation.domain.datasource

import com.automation.data.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun saveMessage(
        msg: String,
        msgType: String,
        phoneNumber: String,
        receivedAt: String,
        isFailed: Boolean,
        receivedDate: String,
        dispatchDate: String?,
        receipePhoneNumber: String?,
        simSlotIndex: Int?,
        senderTitle: String?
    )

    suspend fun updateMessageSendTime(messageId: Long, dispatchTime: String)

    suspend fun getMessages(): List<MessageEntity>
    suspend fun getFailedMessages(): List<MessageEntity>

    suspend fun getMessagesFlow(): Flow<List<MessageEntity>>

    suspend fun updateMessages(vararg messages: MessageEntity)


}