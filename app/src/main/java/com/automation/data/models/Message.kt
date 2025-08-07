package com.automation.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("type")
    val messageType: String,
    @SerialName("content")
    val text: String,
    @SerialName("from")
    val sender: String?,
    @SerialName("to")
    val receiver: String?,
    @SerialName("version")
    val appVersion: String? = null,
    @SerialName("received_at")
    val receivedAt: String?
)

