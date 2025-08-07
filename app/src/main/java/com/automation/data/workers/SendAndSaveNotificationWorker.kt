package com.automation.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.automation.common.utils.DateParser
import com.automation.common.utils.NetworkState
import com.automation.common.utils.NetworkStateProvider
import com.automation.common.utils.getAppVersionName
import com.automation.data.models.Notification
import com.automation.domain.interactors.AppInteractor
import com.automation.domain.models.MessageType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class SendAndSaveNotificationWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val TAG = "SendAndSaveNotificationWorker"
        const val NOTIFICATION_DATA_KEY = "notification_data"
    }

    val appInteractor: AppInteractor by KoinJavaComponent.inject(AppInteractor::class.java)

    private val sdf2 = SimpleDateFormat("dd.MM.yyyy в HH:mm:ss ", Locale.getDefault())


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Обработка исключения
        Timber.tag("NotificationListenerService").e("Exception: " + throwable)
    }

    override suspend fun doWork(): Result {
        Timber.tag("workManager - SendAndSaveNotificationWorker").d("Start")
        return try {
            withContext(exceptionHandler + Dispatchers.IO) {
                val notificationJson = inputData.getString(NOTIFICATION_DATA_KEY)
                val oneNotification =
                    Json.decodeFromString<Notification>(notificationJson ?: "")
                notificationProcessing(oneNotification)
            }
        } catch (e: Exception) {
            Timber.tag("SendAndSaveNotificationWorker").e("Exception: " + e.localizedMessage)
            Result.failure()
        }
    }

    private suspend fun notificationProcessing(oneNotification: Notification): Result {

        return if (NetworkStateProvider.state.value == NetworkState.UNAVAILABLE)
            saveNotification(oneNotification, true)
        else {
            appInteractor.userPhoneNumber?.let { userPhoneNumber ->
                runCatching {
                    appInteractor.sendMessages(
                        oneNotification.toOneMessage(
                            receiver = userPhoneNumber,
                            appVersion = context.getAppVersionName()
                        )
                    )
                }.fold(
                    onSuccess = {
                        Timber.tag(TAG).d("Send Notification success")
                        saveNotification(oneNotification, false)
                    },
                    onFailure = {
                        Timber.tag(TAG).e(it, "Send message failed")
                        saveNotification(oneNotification, true)
                    }
                )
            } ?: run {
                // Обработка случая, когда userPhoneNumber равно null
                Timber.tag(TAG).e("User phone number is null, cannot send message")
                saveNotification(oneNotification, true)
            }
        }
    }

    private suspend fun saveNotification(
        oneNotification: Notification,
        isFailure: Boolean,
    ): Result {
        appInteractor.saveMessage(
            msg = oneNotification.message ?: "Empty",
            msgType = MessageType.Notification.value,
            phoneNumber = oneNotification.senderPackage ?: "PUSH_Unknown",
            receivedAt = oneNotification.receivedAt,
            isFailed = isFailure,
            receivedDate = sdf2.format(System.currentTimeMillis()),
            receipePhoneNumber = appInteractor.userPhoneNumber,
            simSlotIndex = null,
            senderTitle = oneNotification.senderTitle
        )
        return if (isFailure) Result.failure() else Result.success()
    }
}