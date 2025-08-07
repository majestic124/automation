package com.automation

import com.automation.presentation.screens.allMessagesListScreen.utils.DateMapper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateParserTest {

    @Test
    fun testFormatReceivedAtForToday() {
        val today = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        val todayStr = sdf.format(today.time)

        val formattedDate = DateMapper.formatMessageTime(todayStr)
        println("Formatted date: $formattedDate")

        val expectedFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val expected = expectedFormat.format(today.time)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun testFormatReceivedAtForYesterday() {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        val yesterdayStr = sdf.format(yesterday.time)

        val formattedDate = DateMapper.formatMessageTime(yesterdayStr)
        println("Formatted date: $formattedDate")

        val expectedFormat = SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault())
        val expected = expectedFormat.format(yesterday.time)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun testFormatReceivedAtForOlderDate() {
        val weekAgo = Calendar.getInstance()
        weekAgo.add(Calendar.DAY_OF_YEAR, -7)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        val weekAgoStr = sdf.format(weekAgo.time)

        val formattedDate = DateMapper.formatMessageTime(weekAgoStr)
        println("Formatted date: $formattedDate")

        val expectedFormat = SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault())
        val expected = expectedFormat.format(weekAgo.time)
        assertEquals(expected, formattedDate)
    }

    @Test
    fun testFormatReceivedAtWithInvalidFormat() {
        val invalidDate = "not-a-date"

        val result = DateMapper.formatMessageTime(invalidDate)
        println("Formatted date: $result")

        assertEquals(invalidDate, result)
    }
}