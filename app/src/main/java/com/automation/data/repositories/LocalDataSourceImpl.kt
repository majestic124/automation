package com.automation.data.repositories

import android.content.SharedPreferences
import com.automation.data.database.dao.MessagesDao
import com.automation.data.database.entity.MessageEntity
import com.automation.domain.datasource.LocalDataSource
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val preferences: SharedPreferences,
    private val messagesDb: MessagesDao
) : LocalDataSource {
    override suspend fun saveMessage(
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
    ) {
        messagesDb.insertAll(
            MessageEntity(
                message = msg,
                messageType = msgType,
                sender = phoneNumber,
                receivedAt = receivedAt,
                isFailed = isFailed,
                receivedDate = receivedDate,
                dispatchDate = dispatchDate,
                receipePhoneNumber = receipePhoneNumber,
                simSlotIndex = simSlotIndex,
                senderTitle = senderTitle
            )
        )
    }

    override suspend fun updateMessageSendTime(messageId: Long, dispatchTime: String) {
        messagesDb.updateDispatchDate(messageId, dispatchTime)
    }

    override suspend fun getMessages(): List<MessageEntity> {
        return messagesDb.getAll()
    }

    override suspend fun getMessagesFlow(): Flow<List<MessageEntity>> {
        return messagesDb.getFlowAllMessages()
    }

    override suspend fun getFailedMessages(): List<MessageEntity> {
        return messagesDb.getFailedMessages()
    }

    override suspend fun updateMessages(vararg messages: MessageEntity) {
        messagesDb.updateMessages(*messages)
    }
}