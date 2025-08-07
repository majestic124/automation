package com.automation.presentation.screens.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automation.data.api.UrlSettingsManager
import com.automation.domain.interactors.AppInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val appInteractor: AppInteractor,
    private val urlSettingsManager: UrlSettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun updateLink(text: String) {
        _uiState.value = _uiState.value.copy(urlLink = text)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(userPhoneNumber = phone.ifEmpty { null })
    }

    fun clickSaveButton(): Boolean {
        appInteractor.userPhoneNumber = uiState.value.userPhoneNumber
        return updateBaseUrl(_uiState.value.urlLink ?: "")
    }

    private fun ensureHttpsPrefix(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
    }

    private fun ensureSingleSlashSuffix(url: String): String {
        return url.trimEnd('/') + "/"
    }

    private fun formatUrl(url: String): String {
        return ensureSingleSlashSuffix(ensureHttpsPrefix(url))
    }

    private fun isValidBaseUrl(url: String): Boolean {
        return try {
            val parsedUrl = URL(formatUrl(url))
            Timber.e("parsed url $url parsedUrl - $parsedUrl")
            parsedUrl.protocol == "http" || parsedUrl.protocol == "https"
        } catch (e: MalformedURLException) {
            false
        }
    }

    private fun updateBaseUrl(url: String): Boolean {
        return if (isValidBaseUrl(url)) {
            urlSettingsManager.setBaseUrl(formatUrl(url))
            true
        } else {
            showErrorMessage("Неверный URL")
            false
        }
    }

    private fun showErrorMessage(msg: String) {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(toastMessage = msg)
            delay(1.seconds)
            _uiState.value = uiState.value.copy(toastMessage = null)
        }
    }

    init {
        val savedUrl = urlSettingsManager.getBaseUrl()
        _uiState.value = uiState.value.copy(
            urlLink = savedUrl
                .removePrefix("https://")
                .trimEnd('/'),
            userPhoneNumber = appInteractor.userPhoneNumber
        )
    }

    data class UiState(
        val urlLink: String? = null,
        val userPhoneNumber: String? = null,
        val toastMessage: String? = null
    )
}
