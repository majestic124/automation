package com.automation.data.receivers

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import com.automation.common.utils.DateParser
import com.automation.common.utils.NetworkState
import com.automation.common.utils.NetworkStateProvider
import com.automation.common.utils.getAppVersionName
import com.automation.common.utils.goAsync
import com.automation.data.models.Message
import com.automation.domain.interactors.AppInteractor
import com.automation.domain.models.MessageType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class IncomingReceiver : BroadcastReceiver(), KoinComponent {
    companion object {
        private val TAG = IncomingReceiver::class.simpleName.toString()
    }

    private val appInteractor: AppInteractor by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: error("IncomingReceiver context is null")
        val bundle = intent!!.extras
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            // Обработка исключения
            Timber.tag(TAG).e("Exception: %s", throwable)
        }

        try {
            when (intent.action) {
                SMS_RECEIVED_ACTION -> {
                    if (bundle == null) return
                    Timber.tag(TAG).e("onReceive bundle $bundle")

                    val pdusObj = bundle["pdus"] as Array<*>?

                    var phoneNumber = ""
                    val appVersion = context.getAppVersionName()
                    var message = StringBuilder()
//                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS XXX")
                        .withZone(ZoneId.systemDefault())
                    val sdf2 = SimpleDateFormat("dd.MM.yyyy в HH:mm:ss ", Locale.getDefault())
                    var receivedAt = ""
                    var receivedDate = ""
                    var recipientPhoneNumber: String? = null
                    var simSlotIndex: Int? = null
                    for (i in pdusObj!!.indices) {
                        val format = bundle.getString("format")
                        val currentMessage =
                            SmsMessage.createFromPdu(pdusObj[i] as ByteArray?, format)
                        phoneNumber = currentMessage.displayOriginatingAddress
                        message = message.append(currentMessage.displayMessageBody)
                        receivedAt =
                            formatter.format(Instant.ofEpochMilli(currentMessage.timestampMillis))
                        receivedDate = sdf2.format(Date(currentMessage.timestampMillis))
                        val subscriptionId = intent.getIntExtra("subscription", -1)

                        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && context.checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                        ) {


                            val subscriptionManager =
                                context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                            val telephonyManager =
                                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            val activeSubscriptionInfo =
                                subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
                            if (activeSubscriptionInfo != null) {
                                recipientPhoneNumber = appInteractor.userPhoneNumber
                                simSlotIndex =
                                    if (activeSubscriptionInfo.simSlotIndex == 0) 1 else 2
//                                    Timber.tag("SMS_RECEIVER").d(
//                                        "SMS received on SIM slot: " + simSlotIndex + ", phone number: " + receipePhoneNumber + "\n" + "activeSubscriptionInfo - " + activeSubscriptionInfo + ", telephonyManager.line1Number - " + telephonyManager.line1Number + "\n" + "READ_PHONE_NUMBERS - " + context.checkSelfPermission(
//                                            Manifest.permission.READ_PHONE_NUMBERS
//                                        ) + "\n" + "READ_PHONE_STATE " + context.checkSelfPermission(
//                                            Manifest.permission.READ_PHONE_STATE
//                                        )
//                                    )
                            }
                        }

                    }

                    if (NetworkStateProvider.state.value == NetworkState.UNAVAILABLE) {
                        goAsync(exceptionHandler + Dispatchers.IO) {
                            appInteractor.saveMessage(
                                msg = message.toString(),
                                msgType = MessageType.Sms.value,
                                phoneNumber = phoneNumber,
                                receivedAt = receivedAt,
                                isFailed = true,
                                receivedDate = receivedDate,
                                receipePhoneNumber = recipientPhoneNumber,
                                simSlotIndex = simSlotIndex
                            )
                        }
                    } else {
                        Timber.tag(TAG)
                            .d("senderNum: " + phoneNumber + "; message: " + message)

                        val oneMessage = Message(
                            text = message.toString(),
                            receiver = recipientPhoneNumber,
                            sender = phoneNumber,
                            appVersion = appVersion,
                            messageType = MessageType.Sms.value,
                            receivedAt = receivedAt
                        )
                        if (recipientPhoneNumber.isNullOrEmpty()) {
                            goAsync(exceptionHandler + Dispatchers.IO) {
                                appInteractor.saveMessage(
                                    msg = message.toString(),
                                    msgType = MessageType.Sms.value,
                                    phoneNumber = phoneNumber,
                                    receivedAt = receivedAt,
                                    isFailed = true,
                                    receivedDate = receivedDate,
                                    receipePhoneNumber = recipientPhoneNumber,
                                    simSlotIndex = simSlotIndex
                                )
                                Timber.tag(TAG)
                                    .e("receipePhoneNumber isNullOrEmpty")
                            }
                            return
                        }

                        goAsync(exceptionHandler + Dispatchers.IO) {
                            runCatching {
                                appInteractor.sendMessages(
                                    oneMessage,
                                )
                            }
                                .onFailure {
                                    appInteractor.saveMessage(
                                        msg = message.toString(),
                                        msgType = MessageType.Sms.value,
                                        phoneNumber = phoneNumber,
                                        receivedAt = receivedAt,
                                        isFailed = true,
                                        receivedDate = receivedDate,
                                        receipePhoneNumber = recipientPhoneNumber,
                                        simSlotIndex = simSlotIndex
                                    )
                                    Timber.tag(TAG)
                                        .e(it, "Send message failed")
                                }
                                .onSuccess {
                                    appInteractor.saveMessage(
                                        message.toString(),
                                        MessageType.Sms.value,
                                        phoneNumber,
                                        receivedAt,
                                        false,
                                        receivedDate,
                                        DateParser().dispatchDate,
                                        recipientPhoneNumber,
                                        simSlotIndex
                                    )
                                    Timber.tag(TAG)
                                        .d("Send message success")
                                }
                        }
                    }
                }

                else -> {
                    return
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e.stackTraceToString())
        }
    }
}