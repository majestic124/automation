package com.automation.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ping(
    @SerialName("phone_number")
    val userPhone: String? = null,
    @SerialName("battery_level")
    val batteryLevel: Int? = null,
    @SerialName("is_charger_connected")
    val powerConnected: Boolean
)