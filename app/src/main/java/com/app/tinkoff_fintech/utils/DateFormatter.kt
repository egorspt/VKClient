package com.app.tinkoff_fintech.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private val currentDate: Calendar = Calendar.getInstance()
    private val yesterdayDate: Calendar =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    private val personDate: Calendar = Calendar.getInstance()

    private const val LONG_PATTERN = "d MMMM yyyy"
    private const val CURRENT_YEAR_PATTERN = "dd MMMM"
    private const val RECENTLY_PATTERN = "HH:mm"

    private const val dividerFormatToday = "Сегодня"
    private const val dividerFormatYesterday = "Вчера"

    private const val dateFormatToday = "сегодня в "
    private const val dateFormatYesterday = "вчера в "
    private const val dateFormatLong = "Был в сети в "
    private const val at = " в "
    private const val space = " "

    private val longFormatter = SimpleDateFormat(LONG_PATTERN, Locale.getDefault())
    private val currentYearFormatter = SimpleDateFormat(CURRENT_YEAR_PATTERN, Locale.getDefault())
    private val recentlyFormatter = SimpleDateFormat(RECENTLY_PATTERN, Locale.getDefault())

    fun lastSeen(long: Long): String {
        personDate.time = Date(long)

        return when {
            currentDate.compareDayTo(personDate) ->
                dateFormatToday + recentlyFormatter.format(personDate.time)
            yesterdayDate.compareDayTo(personDate) ->
                dateFormatYesterday + recentlyFormatter.format(personDate.time)
            else -> return when {
                currentDate.compareYearTo(personDate) ->
                    dateFormatLong + shortMonth(CURRENT_YEAR_PATTERN, personDate.time)
                else -> dateFormatLong + shortMonth(LONG_PATTERN, personDate.time)
            }

        }
    }

    private fun shortMonth(pattern: String, date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val day = dayFormat.format(date)
        val month = monthFormat.format(date).substring(0, 3)
        val year = yearFormat.format(date)
        return when(pattern) {
            CURRENT_YEAR_PATTERN -> day + space + month + at + recentlyFormatter.format(date)
            else -> day + space + month + space + year
        }
    }

    fun datePost(long: Long): String {
        personDate.time = Date(long)

        return when {
            currentDate.compareDayTo(personDate) ->
                dateFormatToday + recentlyFormatter.format(personDate.time)
            yesterdayDate.compareDayTo(personDate) ->
                dateFormatYesterday + recentlyFormatter.format(personDate.time)
            else -> return when {
                currentDate.compareYearTo(personDate) -> shortMonth(CURRENT_YEAR_PATTERN, personDate.time)
                else -> shortMonth(LONG_PATTERN, personDate.time)
            }

        }
    }

    fun dateDivider(long: Long): String {
        personDate.time = Date(long)

        return when {
            currentDate.compareDayTo(personDate) -> dividerFormatToday
            yesterdayDate.compareDayTo(personDate) -> dividerFormatYesterday
            else -> return when {
                currentDate.compareYearTo(personDate) -> currentYearFormatter.format(personDate)
                else -> longFormatter.format(long)
            }
        }
    }

    fun datePerson(date: String): String {
        val parser = SimpleDateFormat("d.m.yyyy", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(parser.parse(date))
    }

    fun compareDate(current: Long, previous: Long): Boolean {
        val currentDate: Calendar = Calendar.getInstance()
        val previousDate: Calendar = Calendar.getInstance()
        currentDate.time = Date(current)
        previousDate.time = Date(previous)
        return currentDate.compareDayTo(previousDate)
    }

    private fun Calendar.compareDayTo(calendar: Calendar): Boolean {
        val currentYear = this.get(Calendar.YEAR)
        val currentDays = this.get(Calendar.DAY_OF_YEAR)
        val personYear = calendar.get(Calendar.YEAR)
        val personDays = calendar.get(Calendar.DAY_OF_YEAR)

        return currentYear == personYear && currentDays == personDays
    }

    private fun Calendar.compareYearTo(calendar: Calendar): Boolean {
        val currentYear = this.get(Calendar.YEAR)
        val personYear = calendar.get(Calendar.YEAR)

        return currentYear == personYear
    }
}