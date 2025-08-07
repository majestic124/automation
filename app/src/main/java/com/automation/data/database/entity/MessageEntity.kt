package com.automation.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.automation.domain.models.MessageItem

@kotlinx.serialization.Serializable
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "messageType") val messageType: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "received_at") val receivedAt: String,
    @ColumnInfo(name = "is_failed") val isFailed: Boolean,
    @ColumnInfo(name = "received_date") val receivedDate: String,
    @ColumnInfo(name = "dispatch_date") val dispatchDate: String? = null,
    @ColumnInfo(name = "receip_phone_number") val receipePhoneNumber: String? = null,
    @ColumnInfo(name = "sim_slot_index") val simSlotIndex: Int? = null,
    @ColumnInfo(name = "senderTitle") val senderTitle: String? = null,
) {

    fun toMessageItems(): MessageItem {
        return MessageItem(
            id = id,
            messageType = messageType,
            sender = sender,
            message = message,
            receivedAt = receivedAt,
            isFailed = isFailed,
            receivedDate = receivedDate,
            dispatchDate = dispatchDate,
            receipePhoneNumber = receipePhoneNumber,
            simSlotIndex = simSlotIndex,
            senderTitle = senderTitle
        )
    }
}