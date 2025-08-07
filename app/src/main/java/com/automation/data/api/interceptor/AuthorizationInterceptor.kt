package com.automation.data.api.interceptor

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class AuthorizationInterceptor(
    private val json: Json,
    private val secretKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBody = originalRequest.body ?: return chain.proceed(originalRequest)

        val buffer = Buffer()
        requestBody.writeTo(buffer)
        val bodyString = buffer.readUtf8()

        val signature = generateHmacSha512(bodyString, secretKey)

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $signature")
            .build()

        return chain.proceed(newRequest)
    }

    private fun generateHmacSha512(data: String, key: String): String {
        val keySpec = SecretKeySpec(key.toByteArray(), "HmacSHA512")
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(keySpec)

        val hmacBytes = mac.doFinal(data.toByteArray())
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
}