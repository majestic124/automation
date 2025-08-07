package com.automation.domain.models

//data class MessageItem(
//    val id: Long? = null,
//    val receiver: String,
//    val messageContent: String,
//    val status: String
//)

data class MessageItem(
    val id: Long? = null,
    val messageType: String,
    val sender: String,
    val message: String,
    val receivedAt: String,
    val isFailed: Boolean,
    val receivedDate: String,
    val highlightText: String? = null,
    val dispatchDate: String? = null,
    val receipePhoneNumber: String? = null,
    val simSlotIndex: Int? = null,
    val senderTitle: String? = null
)
