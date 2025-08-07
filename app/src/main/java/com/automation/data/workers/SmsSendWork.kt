package com.automation.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.automation.BuildConfig
import com.automation.common.utils.BatteryMonitor
import com.automation.common.utils.DateParser
import com.automation.common.utils.NetworkState
import com.automation.common.utils.NetworkStateProvider
import com.automation.data.database.entity.MessageEntity
import com.automation.data.models.Message
import com.automation.data.models.Ping
import com.automation.domain.interactors.AppInteractor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.io.InterruptedIOException
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

class SmsSendWork(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val DEFAULT_TIMEOUT = 9L
        private val TAG = SmsSendWork::class.simpleName.toString()
    }

    private val appInteractor: AppInteractor by inject(AppInteractor::class.java)
    private val batteryMonitor: BatteryMonitor = BatteryMonitor(context)


    override suspend fun doWork(): Result {
        Timber.tag("workManager").e("Start")
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            // Обработка исключения
            Timber.tag(TAG).e("Exception: %s", throwable)
            Result.failure()
        }

        return try {
            withContext(exceptionHandler + Dispatchers.IO) {
                sendSavedMessagesAndClear()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e("Exception: %s", e.localizedMessage)
            Result.failure()
        }
    }

    private suspend fun ping(): Boolean {
        if (appInteractor.userPhoneNumber.isNullOrEmpty()) {
            appInteractor.updatePingStatusManually(false)
            return false
        }
        return runCatching {
            val phone = Ping(
                userPhone = appInteractor.userPhoneNumber,
                batteryLevel = batteryMonitor.getBatteryPercentage(),
                powerConnected = batteryMonitor.isCharging()
            )
            appInteractor.ping(phone)
        }.onSuccess { result ->
            Timber.tag("ping").e("ping successful, result - " + result)
        }.onFailure { throwable ->
            Timber.tag("ping").e(throwable, "ping failed")
        }.getOrDefault(false)
    }

    private suspend fun sendSavedMessagesAndClear(): Result {
        if (NetworkStateProvider.state.value == NetworkState.UNAVAILABLE) {
            appInteractor.updatePingStatusManually(false)
            return Result.failure()
        }
        if (!ping()) return Result.failure()
        val failedMessages = appInteractor.getFailedMessage()
        Timber.tag(TAG).e("Start1")
        if (failedMessages.isEmpty()) return Result.failure()

        val servMessages = failedMessages.map {
            Message(
                text = it.senderTitle?.let { title -> "$title | ${it.message}" } ?: it.message,
                sender = it.sender,
                receiver = it.receipePhoneNumber,
                messageType = it.messageType,
                appVersion = BuildConfig.VERSION_NAME,
                receivedAt = it.receivedAt
            )
        }

        servMessages.forEachIndexed { index, oneMessage ->
            sendFailedMessage(oneMessage, failedMessages[index])
            delay(300.milliseconds)
        }
        return Result.success()
    }

    private suspend fun sendFailedMessage(
        message: Message,
        failedMessage: MessageEntity
    ): Result {
        val phoneNumber = appInteractor.userPhoneNumber
        runCatching {
            appInteractor.sendMessages(
                message.copy(
                    receiver = phoneNumber
                )
            )
        }.onFailure {
            Timber.tag(TAG).d("Start5")

            Timber.tag(TAG).e(it, "sendSavedMessagesAndClear")

            return if (it == InterruptedIOException()) {
                Result.retry()
            } else {
                Result.failure()
            }

        }.onSuccess {
            Timber.tag(TAG).d("Start6")

            Timber.tag(TAG).d("sendSavedMessagesAndClear success")
            updateStateMessages(
                listOf(
                    failedMessage.copy(
                        receipePhoneNumber = phoneNumber
                    )
                ), kotlin.coroutines.coroutineContext
            )
            return Result.success()
        }
        Timber.tag(TAG).d("Start7")
        return Result.success()

    }

    private fun updateStateMessages(
        failedMessages: List<MessageEntity>,
        coroutineContext: CoroutineContext
    ) {
        val mappedFailedMessages = failedMessages.map {
            it.copy(
                isFailed = false,
                dispatchDate = DateParser().dispatchDate
            )
        }

        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            appInteractor.updateMessages(*mappedFailedMessages.toTypedArray())
        }
    }
}