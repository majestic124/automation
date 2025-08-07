package com.automation.data.receivers

import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.automation.data.models.Notification
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import com.automation.data.workers.SendAndSaveNotificationWorker
import com.automation.domain.models.MessageType
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

class NotificationListenerService : NotificationListenerService(), KoinComponent {
    companion object {
        private val TAG = NotificationListenerService::class.simpleName.toString()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Обработка исключения
        Timber.tag(TAG).e("Exception: " + throwable)
    }

    private var titleData: String? = ""
    private var packageName: String? = ""
    private var id: Int? = -1
    private var textData: String? = ""
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS XXX")

    private val scope = CoroutineScope(exceptionHandler + Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).e("СТАРТ")
    }


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName
        val notification = sbn?.notification
        val extras: Bundle? = notification?.extras

        // Основное логирование
        Timber.tag(TAG).e(
            "\n" + "   Notification details:" + "\n" + "   Package: " + packageName + "\n" + "   Channel ID: " + notification?.channelId + "\n" + "   Category: " + notification?.category + "\n" + "   Flags: " + notification?.flags + "\n" + "   Post time: " + sbn?.postTime + "\n" + "   Group key: " + notification?.group + "\n" + "   Sort key: " + notification?.sortKey + "\n" + "   " + "\n" + "   Title: " + extras?.getString(
                "android.title"
            ) + "\n" + "   Text: " + extras?.getCharSequence("android.text") + "\n" + "   " + "\n" + "   Extras keys: " + extras?.keySet()
                ?.joinToString() + "\n" + "   ".trimIndent()
        )

        // Логирование всех ключей из extras
        extras?.keySet()?.forEach { key ->
            Timber.tag(TAG).d("Extra key: " + key + ", value: " + extras.get(key))
        }

        titleData = if (extras?.getString("android.title") != null) {
            extras.getString("android.title")
        } else {
            ""
        }

        textData = if (extras?.getCharSequence("android.text") != null) {
            extras.getCharSequence("android.text").toString()
        } else {
            ""
        }

        id = sbn?.id

        this.packageName = packageName

        try {
            val oneNotification = Notification(
                id = UUID.randomUUID().toString(),
                senderPackage = "$packageName",
                senderTitle = titleData,
                message = textData,
                receivedAt = formatter.format(
                    Instant.ofEpochMilli(System.currentTimeMillis())
                        .atZone(ZoneId.systemDefault())
                ),
                messageType = MessageType.Notification.value
            )

            processNotification(oneNotification)

            // Логируем созданное уведомление
            Timber.tag(TAG).d("Created notification: " + oneNotification)

        } catch (e: Exception) {
            Timber.tag(TAG).e(e.stackTraceToString())
        }

        Timber.tag("Package").d(packageName.toString())
        Timber.tag("Title").d(titleData.toString())
        Timber.tag("Text").d(textData.toString())
        Timber.tag("Id").d(id.toString())

        sbn?.key?.let { cancelNotification(it) }
    }


    private fun processNotification(oneNotification: Notification) {
        scope.launch {
            if (oneNotification.senderTitle.isNullOrEmpty() && oneNotification.message.isNullOrEmpty()) return@launch

            Timber.tag("NotificationFilter")
                .d("oneNotification.senderPackage: " + oneNotification.senderPackage)


            Timber.tag(TAG)
                .e("notificationProcessing - oneNotification " + oneNotification.toString())

            val oneNotificationJson = Json.encodeToString(oneNotification)
            val data = Data.Builder()
                .putString(
                    SendAndSaveNotificationWorker.NOTIFICATION_DATA_KEY,
                    oneNotificationJson
                )
                .build()

            val notificationWork = OneTimeWorkRequestBuilder<SendAndSaveNotificationWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(this@NotificationListenerService).enqueue(notificationWork)
            return@launch
        }
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn != null) {
            // Логируем информацию об удаленном уведомлении
            Timber.tag("NotificationRemoved")
                .d("Notification removed: " + sbn.packageName + ", ID: " + sbn.id + ", Tag: " + sbn.tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag("NotificationListenerService").d("Destroy Notif Service");
    }
}