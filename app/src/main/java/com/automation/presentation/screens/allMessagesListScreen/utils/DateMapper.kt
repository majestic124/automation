package com.automation.presentation.screens.allMessagesListScreen.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateMapper {

    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS XXX")
        .withZone(ZoneId.systemDefault())

    private val legacyInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
        .withZone(ZoneId.systemDefault())

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    /**
     * Форматирует время из строки формата "yyyy-MM-dd HH:mm:ss.SSSSSS XXX"
     * в удобочитаемый формат для отображения
     */
    fun formatMessageTime(receivedAt: String): String {
        try {
            val instant = parseDateTime(receivedAt)
                ?: return receivedAt

            val now = Instant.now()
            val today = now.truncatedTo(ChronoUnit.DAYS)
            val yesterday = today.minus(1, ChronoUnit.DAYS)
            val messageDay = instant.truncatedTo(ChronoUnit.DAYS)

            return when {
                messageDay == today -> timeFormatter.format(instant)
                else -> dateTimeFormatter.format(instant)
            }
        } catch (e: Exception) {
            return receivedAt
        }
    }

    /**
     * Пытается парсить дату-время из строки в разных форматах
     */
    private fun parseDateTime(dateTimeString: String): Instant? {
        return try {
            Instant.from(inputFormatter.parse(dateTimeString))
        } catch (e: Exception) {
            try {
                Instant.from(legacyInputFormatter.parse(dateTimeString))
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Создает строку с текущим временем в формате "yyyy-MM-dd HH:mm:ss.SSSSSS XXX"
     */
    fun getCurrentTimeFormatted(): String {
        return inputFormatter.format(Instant.now())
    }

    /**
     * Форматирует timestamp в строку формата "yyyy-MM-dd HH:mm:ss.SSSSSS XXX"
     */
    fun formatTimestampForStorage(timestampMillis: Long): String {
        return inputFormatter.format(Instant.ofEpochMilli(timestampMillis))
    }

    /**
     * Парсит строку формата "yyyy-MM-dd HH:mm:ss.SSSSSS XXX" в timestamp
     */
    fun parseToTimestamp(dateTimeString: String): Long {
        val instant = parseDateTime(dateTimeString)
        return instant?.toEpochMilli() ?: 0L
    }
}