package com.automation.data.api

import com.automation.data.models.Message
import com.automation.data.models.Ping
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface KeeperService {
    @POST("api/v1/device/messages")
    suspend fun sendMessage(
        @Body data: Message
    ): Response<Unit>

    @POST("api/v1/device/pings")
    suspend fun ping (
        @Body data: Ping
    ): Response<Unit>
}