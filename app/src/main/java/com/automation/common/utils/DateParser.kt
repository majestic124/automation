package com.automation.common.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateParser(private var date: String? = null) {

    private val calendar = Calendar.getInstance()
    private val currentTime: Long by lazy {
        getTimeInMillis()
    }
    private val currentDate by lazy {
        Date(currentTime)
    }
    val formattedTime: String
        get() = format.format(currentDate)

    val dispatchDate: String by lazy {
        getCurrentDispatchDate()
    }


    private fun getTimeInMillis(): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        val format = SimpleDateFormat("dd.MM.yyyy в HH:mm:ss ", Locale.getDefault())
        val dateTime = date?.let { format.parse(it) }
        return dateTime?.time ?: 0
    }

    // Устанавливаем формат даты и времени для вывода
    private val format: SimpleDateFormat = if (isToday(currentDate)) {
        // Если время сегодняшнее, выводим только время
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    } else if (isYesterday(currentDate)) {
        // Если время вчерашнее, выводим "Вчера" и время
        SimpleDateFormat("'Вчера,' HH:mm:ss", Locale.getDefault());
    } else {
        // Если время позавчера и далее, выводим дату и время
        SimpleDateFormat("dd.MM.yy, HH:mm:ss", Locale.getDefault());
    }

    // Форматируем дату и время
    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val other = Calendar.getInstance()
        other.time = date
        return today.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.YEAR) == other.get(Calendar.YEAR)
    }

    private fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        val other = Calendar.getInstance()
        other.time = date
        return yesterday.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) &&
                yesterday.get(Calendar.YEAR) == other.get(Calendar.YEAR)
    }

    private fun getCurrentDispatchDate(): String {
        return SimpleDateFormat(
            "dd.MM.yyyy 'в' HH:mm:ss",
            Locale.getDefault()
        ).format(Date(System.currentTimeMillis()))
    }
}