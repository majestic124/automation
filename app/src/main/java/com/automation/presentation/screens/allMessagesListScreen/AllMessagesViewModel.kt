package com.automation.presentation.screens.allMessagesListScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automation.domain.interactors.AppInteractor
import com.automation.domain.models.MessageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AllMessagesViewModel (
    private val appInteractor: AppInteractor
): ViewModel() {

    companion object {
        val TAG = AllMessagesViewModel::class.simpleName.toString()
    }

    private val _smsList = MutableStateFlow<List<MessageItem>>(emptyList())
    val smsList = _smsList.asStateFlow()
    private val supervisor = SupervisorJob()

    private val _pingStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val pingStatus: StateFlow<Boolean> = _pingStatus.asStateFlow()

    init {
        getItems()
        observePingStatus()
    }

    private fun observePingStatus() {
        viewModelScope.launch {
            appInteractor.getPingStatusFlow().collect { ping ->
                _pingStatus.value = ping
            }
        }
    }

    private fun getItems() {
        viewModelScope.launch(Dispatchers.IO + supervisor) {
            appInteractor.getMessagesFlow().collect { messages ->
                Timber.tag(TAG).e(" data -%s", messages)
                _smsList.value = sortMessagesByDate(messages.map { it.toMessageItems() })
            }
        }
    }

    private fun sortMessagesByDate(messages: List<MessageItem>): List<MessageItem> {
        val sdfReceivedDate = SimpleDateFormat("dd.MM.yyyy в HH:mm:ss ", Locale.getDefault())
        val sdfReceivedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        return messages.sortedByDescending { message ->
            try {
                val date = if (message.receivedDate.isEmpty()) {
                    sdfReceivedAt.parse(message.receivedAt)
                } else {
                    sdfReceivedDate.parse(message.receivedDate)
                }
                date
            } catch (e: ParseException) {
                null
            }
        }
    }
}