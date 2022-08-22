package net.tesa.ojosapp.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface CalendarUtilsProtocol {
    fun dateOfWeek(): CalendarUtil.DayOfWeek
    fun now(): Date
    fun isSameDay(date: Date): Boolean
    fun datesTimeDifference(date: Date, date1: Date): Long
    fun getPickerValues(): Array<String>
}

class CalendarUtil : CalendarUtilsProtocol {

    companion object {
        const val defaultStartTimeInFormat = "30 minutos"
    }

    enum class DayOfWeek {
        DUMMY,
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }

    override fun dateOfWeek(): DayOfWeek {
        val weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return DayOfWeek.values()[weekday]
    }

    override fun now(): Date {
        return Calendar.getInstance().time
    }

    @SuppressLint("SimpleDateFormat")
    override fun isSameDay(date: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd")
        return fmt.format(date).equals(fmt.format(now()))
    }

    @SuppressLint("DefaultLocale")
    fun getTimeWithCountdownFormat(milliseconds: Long): String {
        //Convert milliseconds into hour,minute and seconds
        val time: String
        if (TimeUnit.MILLISECONDS.toHours(milliseconds).toInt() == 0) {
            time = java.lang.String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        milliseconds
                    )
                ),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        milliseconds
                    )
                )
            )
        } else {
            time = java.lang.String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        milliseconds
                    )
                ),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        milliseconds
                    )
                )
            )
        }
        return time
    }

    override fun datesTimeDifference(date: Date, date1: Date): Long {
        return date1.time - date.time
    }

    override fun getPickerValues(): Array<String> {
        // 5 min to 1:30 horas
        val list = emptyList<String>().toMutableList()
        for (time in 1..90) {
            if (time == 60) list += "1 hora" else
                if (time > 60) {
                    if (time % 5 == 0) {
                        val min = time - 60
                        list += "1 hora $min minutos"
                    }
                } else {
                    if (time % 5 == 0) {
                        list += "$time minutos"
                    }
                }
        }
        return list.toTypedArray()
    }

    fun getMillsFromTimeFormat(time: String): Long {
        // 5 minutos
        // 1 hora 5 minutos
        // 1 hota
        return if (time.contains("hora")) {
            if (time.contains("minutos")) {
                // 60 + min
                val values = time.split("\\s+".toRegex())
                60 + values[2].toLong() * 60 * 1000
            } else {
                60 * 60 * 1000
            }
        } else {
            val values = time.split("\\s+".toRegex())
            values[0].toLong() * 60 * 1000
        }
    }
}