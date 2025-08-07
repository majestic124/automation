package com.automation.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("id")
    val id: String?,
    @SerialName("senderPackage")
    val senderPackage: String?,
    @SerialName("senderTitle")
    val senderTitle: String?,
    @SerialName("message")
    val message: String?,
    @SerialName("receivedAt")
    val receivedAt: String,
    @SerialName("messageType")
    val messageType: String,

    ) {
    fun toOneMessage(receiver: String, appVersion: String): Message {
        val content = senderTitle?.let { title -> "$title | $message" } ?: message
        return Message(
            text = content.toString(),
            receiver = receiver,
            sender = senderPackage ?: "PUSH_Unknown",
            appVersion = appVersion,
            messageType = messageType,
            receivedAt = receivedAt
        )
    }
}